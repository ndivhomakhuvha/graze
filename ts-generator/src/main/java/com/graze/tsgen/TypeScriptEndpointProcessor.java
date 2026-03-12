package com.graze.tsgen;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JSR 269 annotation processor that generates Angular TypeScript code from
 * Spring MVC controllers annotated with {@link TypeScriptEndpoint}.
 * <p>
 * Generated artifacts:
 * <ul>
 *   <li>{@code enums/}   – one {@code export enum} per Java enum</li>
 *   <li>{@code models/}  – one {@code export interface} per DTO class</li>
 *   <li>{@code services/} – one {@code @Injectable} Angular service per controller</li>
 *   <li>{@code index.ts} – barrel re-export</li>
 * </ul>
 * <p>
 * Processor option: {@code -Ats.output=<dir>} (default {@code src/app/shared/api}).
 */
@SupportedAnnotationTypes("com.graze.tsgen.TypeScriptEndpoint")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedOptions("ts.output")
public class TypeScriptEndpointProcessor extends AbstractProcessor {

    // ── Java → TypeScript type map ─────────────────────────────────────

    private static final Map<String, String> KNOWN_TYPES = Map.ofEntries(
            Map.entry("java.lang.String", "string"),
            Map.entry("java.util.UUID", "string"),
            Map.entry("java.lang.Long", "number"),
            Map.entry("java.lang.Integer", "number"),
            Map.entry("java.lang.Double", "number"),
            Map.entry("java.lang.Float", "number"),
            Map.entry("java.lang.Short", "number"),
            Map.entry("java.lang.Byte", "number"),
            Map.entry("java.lang.Character", "string"),
            Map.entry("java.math.BigDecimal", "number"),
            Map.entry("java.math.BigInteger", "number"),
            Map.entry("java.lang.Boolean", "boolean"),
            Map.entry("java.time.LocalDate", "string"),
            Map.entry("java.time.LocalDateTime", "string"),
            Map.entry("java.time.Instant", "string"),
            Map.entry("java.time.ZonedDateTime", "string"),
            Map.entry("java.time.OffsetDateTime", "string")
    );

    private static final Set<String> COLLECTION_TYPES = Set.of(
            "java.util.List", "java.util.Collection", "java.util.Set"
    );

    private static final Set<String> TS_PRIMITIVES = Set.of(
            "string", "number", "boolean", "void", "any"
    );

    // ── State accumulated across annotated controllers ─────────────────

    private final Set<TypeElement> discoveredModels = new LinkedHashSet<>();
    private final Set<TypeElement> discoveredEnums = new LinkedHashSet<>();
    private boolean processed = false;

    // ── Internal records ───────────────────────────────────────────────

    record ControllerMeta(String basePath, String serviceName, String fileName,
                          List<EndpointMeta> endpoints) {}

    record EndpointMeta(String name, String verb, String subPath,
                        String tsReturn, boolean isVoid,
                        List<Param> pathParams, List<Param> queryParams,
                        Param body) {}

    record Param(String name, String tsType, boolean required) {}

    // ═══════════════════════════════════════════════════════════════════
    // Entry point
    // ═══════════════════════════════════════════════════════════════════

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (processed || roundEnv.processingOver()) return false;

        TypeElement marker = processingEnv.getElementUtils()
                .getTypeElement("com.graze.tsgen.TypeScriptEndpoint");
        if (marker == null) return false;

        Set<? extends Element> annotated = roundEnv.getElementsAnnotatedWith(marker);
        if (annotated.isEmpty()) return false;
        processed = true;

        // 1. Parse every annotated controller
        List<ControllerMeta> controllers = new ArrayList<>();
        for (Element el : annotated) {
            if (el.getKind() == ElementKind.CLASS) {
                ControllerMeta meta = parseController((TypeElement) el);
                if (meta != null) controllers.add(meta);
            }
        }

        // 2. Write TypeScript files
        String output = processingEnv.getOptions()
                .getOrDefault("ts.output", "src/app/shared/api");
        Path base = Path.of(output);

        try {
            Files.createDirectories(base.resolve("enums"));
            Files.createDirectories(base.resolve("models"));
            Files.createDirectories(base.resolve("services"));

            for (TypeElement e : discoveredEnums)  generateEnum(base.resolve("enums"), e);
            for (TypeElement m : discoveredModels)  generateModel(base.resolve("models"), m);
            for (ControllerMeta c : controllers)    generateService(base.resolve("services"), c);
            generateIndex(base, controllers);

            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    String.format("[ts-generator] Generated %d enum(s), %d model(s), %d service(s) → %s",
                            discoveredEnums.size(), discoveredModels.size(),
                            controllers.size(), base));
        } catch (IOException ex) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    "[ts-generator] " + ex.getMessage());
        }
        return true;
    }

    // ═══════════════════════════════════════════════════════════════════
    // Parsing
    // ═══════════════════════════════════════════════════════════════════

    private ControllerMeta parseController(TypeElement cls) {
        String basePath = readMappingPath(cls);
        String simple = cls.getSimpleName().toString();
        String svcName = simple.replace("Controller", "Service");
        String fileName = toKebab(simple.replace("Controller", "")) + ".service.ts";

        List<EndpointMeta> endpoints = cls.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.METHOD)
                .map(e -> parseEndpoint((ExecutableElement) e))
                .filter(Objects::nonNull)
                .toList();

        return new ControllerMeta(basePath, svcName, fileName, endpoints);
    }

    private EndpointMeta parseEndpoint(ExecutableElement method) {
        String verb = null;
        String subPath = "";

        for (AnnotationMirror am : method.getAnnotationMirrors()) {
            String ann = am.getAnnotationType().asElement().getSimpleName().toString();
            switch (ann) {
                case "GetMapping"    -> { verb = "GET";    subPath = extractPath(am); }
                case "PostMapping"   -> { verb = "POST";   subPath = extractPath(am); }
                case "PutMapping"    -> { verb = "PUT";    subPath = extractPath(am); }
                case "PatchMapping"  -> { verb = "PATCH";  subPath = extractPath(am); }
                case "DeleteMapping" -> { verb = "DELETE";  subPath = extractPath(am); }
            }
        }
        if (verb == null) return null; // not a handler method

        List<Param> pathParams = new ArrayList<>();
        List<Param> queryParams = new ArrayList<>();
        Param body = null;

        for (VariableElement p : method.getParameters()) {
            String pName = p.getSimpleName().toString();
            String pTs   = toTs(p.asType());
            discover(p.asType());

            if (hasAnnotation(p, "PathVariable")) {
                pathParams.add(new Param(pName, pTs, true));
            } else if (hasAnnotation(p, "RequestParam")) {
                queryParams.add(new Param(pName, pTs, isParamRequired(p)));
            } else if (hasAnnotation(p, "RequestBody")) {
                body = new Param(pName, pTs, true);
            }
        }

        discover(method.getReturnType());
        String tsReturn = toTs(method.getReturnType());
        boolean isVoid  = method.getReturnType().getKind() == TypeKind.VOID;

        return new EndpointMeta(method.getSimpleName().toString(), verb, subPath,
                tsReturn, isVoid, pathParams, queryParams, body);
    }

    // ═══════════════════════════════════════════════════════════════════
    // Java → TypeScript type mapping & recursive type discovery
    // ═══════════════════════════════════════════════════════════════════

    private String toTs(TypeMirror t) {
        return switch (t.getKind()) {
            case VOID    -> "void";
            case BOOLEAN -> "boolean";
            case INT, LONG, DOUBLE, FLOAT, SHORT, BYTE -> "number";
            case CHAR    -> "string";
            case DECLARED -> {
                DeclaredType dt = (DeclaredType) t;
                TypeElement el  = (TypeElement) dt.asElement();
                String fqn = el.getQualifiedName().toString();
                if (KNOWN_TYPES.containsKey(fqn)) yield KNOWN_TYPES.get(fqn);
                if (COLLECTION_TYPES.contains(fqn) && !dt.getTypeArguments().isEmpty())
                    yield toTs(dt.getTypeArguments().getFirst()) + "[]";
                yield el.getSimpleName().toString();
            }
            case ARRAY -> toTs(((ArrayType) t).getComponentType()) + "[]";
            default    -> "any";
        };
    }

    /** Walk a type and register every reachable enum / DTO class. */
    private void discover(TypeMirror t) {
        if (t.getKind() != TypeKind.DECLARED) return;
        DeclaredType dt = (DeclaredType) t;
        TypeElement el  = (TypeElement) dt.asElement();
        String fqn = el.getQualifiedName().toString();

        if (KNOWN_TYPES.containsKey(fqn)) return;
        if (COLLECTION_TYPES.contains(fqn)) {
            if (!dt.getTypeArguments().isEmpty()) discover(dt.getTypeArguments().getFirst());
            return;
        }
        if (el.getKind() == ElementKind.ENUM) {
            discoveredEnums.add(el);
            return;
        }
        if (el.getKind() == ElementKind.CLASS && discoveredModels.add(el)) {
            // Recurse into fields
            el.getEnclosedElements().stream()
                    .filter(e -> e.getKind() == ElementKind.FIELD
                            && !e.getModifiers().contains(Modifier.STATIC))
                    .forEach(e -> discover(e.asType()));
        }
    }

    // ═══════════════════════════════════════════════════════════════════
    // TypeScript file generation
    // ═══════════════════════════════════════════════════════════════════

    // ── Enums ──────────────────────────────────────────────────────────

    private void generateEnum(Path dir, TypeElement el) throws IOException {
        String name = el.getSimpleName().toString();
        List<String> constants = el.getEnclosedElements().stream()
                .filter(e -> e.getKind() == ElementKind.ENUM_CONSTANT)
                .map(e -> e.getSimpleName().toString())
                .toList();

        var sb = new StringBuilder();
        sb.append("// Auto-generated by @TypeScriptEndpoint — do not edit\n\n");
        sb.append("export enum ").append(name).append(" {\n");
        for (int i = 0; i < constants.size(); i++) {
            sb.append("  ").append(constants.get(i))
              .append(" = '").append(constants.get(i)).append("'");
            if (i < constants.size() - 1) sb.append(',');
            sb.append('\n');
        }
        sb.append("}\n");

        writeIfChanged(dir.resolve(toKebab(name) + ".enum.ts"), sb.toString());
    }

    // ── Models (interfaces) ────────────────────────────────────────────

    private void generateModel(Path dir, TypeElement el) throws IOException {
        String name = el.getSimpleName().toString();
        Set<String> enumImports  = new TreeSet<>();
        Set<String> modelImports = new TreeSet<>();
        List<String[]> fields    = new ArrayList<>();

        for (Element enc : el.getEnclosedElements()) {
            if (enc.getKind() == ElementKind.FIELD
                    && !enc.getModifiers().contains(Modifier.STATIC)) {
                fields.add(new String[]{
                        enc.getSimpleName().toString(),
                        toTs(enc.asType())
                });
                collectImports(enc.asType(), enumImports, modelImports, name);
            }
        }

        var sb = new StringBuilder();
        sb.append("// Auto-generated by @TypeScriptEndpoint — do not edit\n\n");
        for (String e : enumImports)
            sb.append("import { ").append(e)
              .append(" } from '../enums/").append(toKebab(e)).append(".enum';\n");
        for (String m : modelImports)
            sb.append("import { ").append(m)
              .append(" } from './").append(toKebab(m)).append(".model';\n");
        if (!enumImports.isEmpty() || !modelImports.isEmpty()) sb.append('\n');

        sb.append("export interface ").append(name).append(" {\n");
        for (String[] f : fields)
            sb.append("  ").append(f[0]).append(": ").append(f[1]).append(";\n");
        sb.append("}\n");

        writeIfChanged(dir.resolve(toKebab(name) + ".model.ts"), sb.toString());
    }

    // ── Services ───────────────────────────────────────────────────────

    private void generateService(Path dir, ControllerMeta ctrl) throws IOException {
        Set<String> enumImp  = new TreeSet<>();
        Set<String> modelImp = new TreeSet<>();
        boolean needsHttpParams = ctrl.endpoints().stream()
                .anyMatch(ep -> !ep.queryParams().isEmpty());

        for (EndpointMeta ep : ctrl.endpoints()) {
            addServiceImport(ep.tsReturn(), enumImp, modelImp);
            if (ep.body() != null) addServiceImport(ep.body().tsType(), enumImp, modelImp);
            ep.queryParams().forEach(q -> addServiceImport(q.tsType(), enumImp, modelImp));
        }

        var sb = new StringBuilder();
        sb.append("// Auto-generated by @TypeScriptEndpoint — do not edit\n\n");

        // Angular imports
        sb.append("import { Injectable, inject } from '@angular/core';\n");
        sb.append("import { HttpClient");
        if (needsHttpParams) sb.append(", HttpParams");
        sb.append(" } from '@angular/common/http';\n");
        sb.append("import { Observable } from 'rxjs';\n");
        sb.append("import { environment } from '../../../../environments/environment';\n");

        // Domain imports
        for (String e : enumImp)
            sb.append("import { ").append(e)
              .append(" } from '../enums/").append(toKebab(e)).append(".enum';\n");
        for (String m : modelImp)
            sb.append("import { ").append(m)
              .append(" } from '../models/").append(toKebab(m)).append(".model';\n");

        sb.append("\n@Injectable({ providedIn: 'root' })\n");
        sb.append("export class ").append(ctrl.serviceName()).append(" {\n");
        sb.append("  private http = inject(HttpClient);\n");
        sb.append("  private baseUrl = `${environment.apiUrl}").append(ctrl.basePath()).append("`;\n");

        for (EndpointMeta ep : ctrl.endpoints()) {
            sb.append('\n');
            emitMethod(sb, ep);
        }

        sb.append("}\n");

        writeIfChanged(dir.resolve(ctrl.fileName()), sb.toString());
    }

    private void emitMethod(StringBuilder sb, EndpointMeta ep) {
        // ── Build method signature params ──
        var sig = new ArrayList<String>();
        ep.pathParams().forEach(p -> sig.add(p.name() + ": " + p.tsType()));
        if (ep.body() != null) sig.add("body: " + ep.body().tsType());

        List<Param> reqQ = ep.queryParams().stream().filter(Param::required).toList();
        List<Param> optQ = ep.queryParams().stream().filter(p -> !p.required()).toList();
        reqQ.forEach(p -> sig.add(p.name() + ": " + p.tsType()));

        if (!optQ.isEmpty()) {
            String optType = "{ " + optQ.stream()
                    .map(p -> p.name() + "?: " + p.tsType())
                    .collect(Collectors.joining("; ")) + " }";
            sig.add("params?: " + optType);
        }

        String ret = ep.isVoid() ? "void" : ep.tsReturn();

        sb.append("  ").append(ep.name()).append('(')
          .append(String.join(", ", sig))
          .append("): Observable<").append(ret).append("> {\n");

        // ── HttpParams ──
        if (!ep.queryParams().isEmpty()) {
            sb.append("    let httpParams = new HttpParams();\n");
            for (Param p : reqQ)
                sb.append("    httpParams = httpParams.set('").append(p.name())
                  .append("', String(").append(p.name()).append("));\n");
            for (Param p : optQ)
                sb.append("    if (params?.").append(p.name())
                  .append(" != null) httpParams = httpParams.set('").append(p.name())
                  .append("', String(params.").append(p.name()).append("));\n");
        }

        // ── URL expression ──
        String url;
        if (ep.subPath().isEmpty()) {
            url = "this.baseUrl";
        } else {
            // Replace {param} → ${param} for JS template literal
            String tpl = ep.subPath().replaceAll("\\{(\\w+)}", "\\${$1}");
            url = "`${this.baseUrl}" + tpl + "`";
        }

        // ── HTTP call ──
        String verb = ep.verb().toLowerCase();
        sb.append("    return this.http.").append(verb)
          .append('<').append(ret).append(">(").append(url);

        // Body argument (POST / PUT / PATCH always need a body argument)
        if (Set.of("POST", "PUT", "PATCH").contains(ep.verb())) {
            sb.append(", ").append(ep.body() != null ? "body" : "null");
        }

        // Options with query params
        if (!ep.queryParams().isEmpty()) {
            sb.append(", { params: httpParams }");
        }

        sb.append(");\n");
        sb.append("  }\n");
    }

    // ── Barrel index ───────────────────────────────────────────────────

    private void generateIndex(Path base, List<ControllerMeta> controllers) throws IOException {
        var sb = new StringBuilder();
        sb.append("// Auto-generated by @TypeScriptEndpoint — do not edit\n\n");

        for (TypeElement e : discoveredEnums)
            sb.append("export { ").append(e.getSimpleName())
              .append(" } from './enums/").append(toKebab(e.getSimpleName().toString()))
              .append(".enum';\n");
        if (!discoveredEnums.isEmpty()) sb.append('\n');

        for (TypeElement m : discoveredModels)
            sb.append("export type { ").append(m.getSimpleName())
              .append(" } from './models/").append(toKebab(m.getSimpleName().toString()))
              .append(".model';\n");
        if (!discoveredModels.isEmpty()) sb.append('\n');

        for (ControllerMeta c : controllers)
            sb.append("export { ").append(c.serviceName())
              .append(" } from './services/")
              .append(toKebab(c.serviceName().replace("Service", "")))
              .append(".service';\n");

        writeIfChanged(base.resolve("index.ts"), sb.toString());
    }

    // ═══════════════════════════════════════════════════════════════════
    // Helpers
    // ═══════════════════════════════════════════════════════════════════

    private void collectImports(TypeMirror t, Set<String> enumImp,
                                Set<String> modelImp, String self) {
        if (t.getKind() != TypeKind.DECLARED) return;
        DeclaredType dt = (DeclaredType) t;
        TypeElement el  = (TypeElement) dt.asElement();
        String fqn = el.getQualifiedName().toString();
        if (KNOWN_TYPES.containsKey(fqn)) return;
        if (COLLECTION_TYPES.contains(fqn)) {
            if (!dt.getTypeArguments().isEmpty())
                collectImports(dt.getTypeArguments().getFirst(), enumImp, modelImp, self);
            return;
        }
        String name = el.getSimpleName().toString();
        if (name.equals(self)) return;
        if (el.getKind() == ElementKind.ENUM)        enumImp.add(name);
        else if (el.getKind() == ElementKind.CLASS)  modelImp.add(name);
    }

    private void addServiceImport(String tsType, Set<String> enumImp, Set<String> modelImp) {
        String base = tsType.replace("[]", "");
        if (TS_PRIMITIVES.contains(base)) return;
        if (discoveredEnums.stream().anyMatch(e -> e.getSimpleName().toString().equals(base)))
            enumImp.add(base);
        else if (discoveredModels.stream().anyMatch(m -> m.getSimpleName().toString().equals(base)))
            modelImp.add(base);
    }

    // ── Annotation introspection ───────────────────────────────────────

    private boolean hasAnnotation(Element el, String simpleName) {
        return el.getAnnotationMirrors().stream()
                .anyMatch(am -> am.getAnnotationType().asElement()
                        .getSimpleName().toString().equals(simpleName));
    }

    private AnnotationMirror findAnnotation(Element el, String simpleName) {
        return el.getAnnotationMirrors().stream()
                .filter(am -> am.getAnnotationType().asElement()
                        .getSimpleName().toString().equals(simpleName))
                .findFirst().orElse(null);
    }

    /** Read the first {@code value} or {@code path} from a mapping annotation. */
    private String extractPath(AnnotationMirror am) {
        for (var entry : am.getElementValues().entrySet()) {
            String key = entry.getKey().getSimpleName().toString();
            if ("value".equals(key) || "path".equals(key)) {
                Object val = entry.getValue().getValue();
                if (val instanceof String s) return s;
                if (val instanceof List<?> list && !list.isEmpty())
                    return ((AnnotationValue) list.getFirst()).getValue().toString();
            }
        }
        return "";
    }

    /** Read the class-level {@code @RequestMapping} path. */
    private String readMappingPath(TypeElement cls) {
        AnnotationMirror am = findAnnotation(cls, "RequestMapping");
        return am != null ? extractPath(am) : "";
    }

    /**
     * Determines if a {@code @RequestParam} is required.
     * A param is optional when {@code required = false} or
     * when a {@code defaultValue} is explicitly set.
     */
    private boolean isParamRequired(VariableElement param) {
        AnnotationMirror am = findAnnotation(param, "RequestParam");
        if (am == null) return true;

        boolean hasDefault  = false;
        Boolean reqExplicit = null;

        // Only look at explicitly-set values (not annotation defaults)
        for (var entry : am.getElementValues().entrySet()) {
            String key = entry.getKey().getSimpleName().toString();
            if ("defaultValue".equals(key)) hasDefault = true;
            if ("required".equals(key))     reqExplicit = (Boolean) entry.getValue().getValue();
        }

        if (hasDefault) return false;           // Spring treats defaultValue as implicitly optional
        return reqExplicit != null ? reqExplicit : true;  // annotation default is true
    }

    // ── Naming ─────────────────────────────────────────────────────────

    /** Convert {@code PascalCase} / {@code camelCase} to {@code kebab-case}. */
    private String toKebab(String name) {
        return name
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .replaceAll("([A-Z]+)([A-Z][a-z])", "$1-$2")
                .toLowerCase();
    }

    // ── File I/O ───────────────────────────────────────────────────────

    /** Write only when content differs — avoids triggering Angular file watchers. */
    private void writeIfChanged(Path path, String content) throws IOException {
        if (Files.exists(path)) {
            String existing = Files.readString(path);
            if (existing.equals(content)) return;
        }
        Files.writeString(path, content);
    }
}

