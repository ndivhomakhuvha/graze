# AGENTS.md — Graze Livestock Management

## Architecture

Full-stack monorepo: **Spring Boot 4 (Java 25)** backend + **Angular 20** frontend. The Angular build outputs to `target/classes/static/` so the Spring Boot jar serves the SPA.

**Backend** uses **Spring Modulith** for enforced module boundaries. Each business module lives under `src/main/java/com/graze/graze/{module}/` with this internal structure:
- `domain/` — JPA entities, DTOs (`dto/`), enums (`enums/`), mappers (`mapper/`), repositories (`repository/`)
- `application/` — Service layer
- `web/` — REST controllers
- `events/` — Domain event records
- `listeners/` — Cross-module event handlers

Module dependency rules are declared in each module's `package-info.java` via `@ApplicationModule(allowedDependencies = {...})`. The `ModularityTests.java` test enforces these boundaries — run it before adding cross-module imports.

**Modules and their allowed dependencies:**
- `animal` → `common::exceptions`, `user::domain`, `user::repository`, `user::application`
- `health` → `animal::domain`, `animal::repository`, `animal::events`, `common::exceptions`
- `finances` → `common::exceptions`, `health::events`
- `user` — standalone
- `common` — shared (exceptions only)

**Cross-module communication** uses Spring application events, not direct service calls:
- `animal` publishes `AnimalRegistered` → `health` listens to create an initial health record
- `health` publishes `TreatmentAdministered` → `finances` listens to record veterinary expenses

## Key Conventions

- **Entity-to-DTO mapping**: Use MapStruct interfaces (`@Mapper(componentModel = "spring")`). Parent/relationship fields are resolved manually in the service, not in the mapper. See `AnimalMapper.java`.
- **ID generation**: `Animal` uses a custom Hibernate `@AnimalTagId` generator (`AnimalTagGenerator`) that produces gender-prefixed sequential tags (e.g., `M001`, `F002`) from a `graze.animal_sequence` DB table.
- **Security**: Keycloak OAuth2 JWT. Roles extracted via `KeycloakRoleConverter` from the JWT `roles` claim. Controllers use `@PreAuthorize("hasRole('...')")` — roles include `manage-animal`, `view-animal`, `manage-health`, `view-health`, `manage-finances`, `view-finances`.
- **Exceptions**: Throw `ResourceNotFoundException` for 404s, `IllegalArgumentException` for 400s. Both are handled by `GlobalExceptionHandler` returning `{timestamp, status, error, message}`.
- **DB migrations**: Flyway in `src/main/resources/db/migration/`. Naming: `V<N>__<description>.sql`. All tables live in the `graze` schema. JPA `ddl-auto: validate` — schema changes MUST go through Flyway.

## Dev Setup & Commands

```bash
# Start Postgres (port 5433) and Keycloak (port 8180)
docker compose -f docker/docker-compose.yml up -d

# Backend (port 8080)
./mvnw spring-boot:run

# Frontend dev server (port 4200, proxies API to 8080)
npm start

# Build Angular into Spring Boot's static resources
npm run build

# Run all backend tests
./mvnw test

# Verify module boundaries only
./mvnw test -Dtest=ModularityTests
```

## Frontend (Angular)

- Zoneless change detection (`provideZonelessChangeDetection()`)
- Auth: `KeycloakService` wraps `keycloak-js`; initialized via `APP_INITIALIZER`. `authInterceptor` attaches JWT bearer tokens. `authGuard`/`roleGuard` protect routes.
- Environment configs in `src/environments/` — Keycloak URL, realm, clientId, apiUrl
- Component prefix: `graze` (configured in `angular.json`)
- Prettier with Angular HTML parser configured in `package.json`

## API Endpoints

| Module   | Base Path          | Roles                              |
|----------|--------------------|-------------------------------------|
| Animal   | `/animals`         | `view-animal`, `manage-animal`      |
| Health   | `/health-records`  | `view-health`, `manage-health`      |
| Finance  | `/finances`        | `view-finances`, `manage-finances`  |
| User     | `/users`           | (see controller)                    |

Swagger UI available at `/swagger-ui.html` (unauthenticated). Use "Authorize" button with a Keycloak JWT.

