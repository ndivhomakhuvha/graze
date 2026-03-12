package com.graze.tsgen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a Spring {@code @RestController} for TypeScript code generation.
 * <p>
 * During compilation the {@link TypeScriptEndpointProcessor} scans every
 * controller annotated with {@code @TypeScriptEndpoint} and emits:
 * <ul>
 *   <li>Angular {@code @Injectable} service classes (one per controller)</li>
 *   <li>TypeScript {@code interface}s for every DTO reachable from method signatures</li>
 *   <li>TypeScript {@code enum}s for every Java enum reachable from method signatures</li>
 *   <li>A barrel {@code index.ts} re-exporting all generated artifacts</li>
 * </ul>
 * <p>
 * Output directory is controlled by the compiler option {@code -Ats.output=path}
 * (default: {@code src/app/shared/api}).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface TypeScriptEndpoint {
}

