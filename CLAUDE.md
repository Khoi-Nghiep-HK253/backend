# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Divvy Backend — a Spring Boot service for group expense management (expense splitting, debt tracking, settlement recording). Java 26, Spring Boot 4.1.0, PostgreSQL 16, Flyway, MapStruct, Lombok, JWT auth. Base package: `com.hcmut.divvy`.

## Commands

```bash
# Compile
./gradlew compileJava

# Run tests / a single test class or method
./gradlew test
./gradlew test --tests "com.hcmut.divvy.SomeClassTest"
./gradlew test --tests "com.hcmut.divvy.SomeClassTest.someMethod"

# Run the app locally (needs DB running — see below)
./gradlew bootRun

# Build executable JAR
./gradlew bootJar

# Check code formatting (fails if any file violates the style profile)
./gradlew spotlessCheck

# Auto-fix code formatting
./gradlew spotlessApply

# Full stack via Docker (db + backend)
docker-compose up -d --build

# DB only (for local Gradle dev)
docker-compose up -d db
```

Swagger UI is served at `/swagger-ui.html` (OpenAPI JSON at `/v3/api-docs`) once running.

Formatting is enforced via the Spotless Gradle plugin using an Eclipse formatter profile at `config/spotless/eclipse-java-formatter.xml` (4-space indent, no tabs; deliberately does not reflow lines/comments the author already wrapped — it only normalizes indentation/whitespace). Run `spotlessApply` before committing if you're unsure about formatting.

### Database migrations (entity-first / Prisma-like workflow)

Java `@Entity` classes are the source of truth. Do not hand-write Flyway SQL — generate it from entity diffs:

1. Edit/add fields on a `@Entity` class.
2. Run `./gradlew migrateDev -PmigrationName=describe_change` (requires the DB running). This diffs entities against the live schema via `com.hcmut.divvy.generator.MigrationGenerator` and writes a timestamped file to `src/main/resources/db/migration/V<timestamp>__describe_change.sql`.
3. Run `./gradlew bootRun` — Flyway applies the new migration and Hibernate's `ddl-auto=validate` confirms the entity/schema match exactly.
4. Commit both the entity change and the generated migration SQL together.

Dev-only data: with the `dev` Spring profile active, `DevDataSeeder` (`src/main/java/com/hcmut/divvy/config/DevDataSeeder.java`) auto-populates sample data (seed accounts share password `123456`; see README for the list). `src/main/resources/dev-data.sql` / `clean-data.sql` support this seeding/reset.

## Architecture

Layered architecture with a **Single Model Parameter Pattern** (one command object per service method) and a **pure, decoupled validator layer**. Request flow:

```
Controller → MapStruct Mapper → Single Command Model → Service (interface) → Service Impl
   Service Impl fetches entities via Repository, then hands entities to Validators for rule checks
   → Repository (JPA) → Entity → PostgreSQL
```

Package layout (`src/main/java/com/hcmut/divvy/`):

- `controller` — REST controllers. Validate input with `@Valid`, map DTO + auth + path params to a single Command Model via MapStruct, call the service interface, return `ApiResponse<T>` (`common/dto/ApiResponse.java`).
- `service` — Service interfaces (business contracts).
- `service/impl` — Service implementations. Act as orchestrators: fetch entities from repositories, pass them to validators for rule checks, then persist.
- `service/model` — Single command/parameter models (e.g. `CreateGroupModel`, `LoginModel`) — each service method takes exactly one of these, bundling request body + path params + the authenticated user.
- `validator` — Pure rule-assertion classes with **no repository injection** (e.g. `UserValidator`, `GroupValidator`, `GroupMemberValidator`, `InvitationValidator`, `ExpenseValidator`, `DebtValidator`, `SettlementValidator`, `CategoryValidator`, `CurrencyValidator`, `PasswordResetValidator`). They receive already-fetched entities/state from the service and throw on rule violations — this keeps them trivially unit-testable without mocking a repository, and avoids duplicate DB fetches.
- `repository` — Spring Data JPA repositories.
- `entity` / `entity/enums` — JPA entities (all extend `BaseEntity` for `created_at`/`updated_at` auditing via JPA Auditing) and strongly-typed enums (`UserRole`, `GroupRole`, `InvitationStatus`, `DebtStatus`, `SplitType`, ...).
- `mapper` — MapStruct mappers between Request DTOs, Command Models, Entities, and Response DTOs.
- `dto/request`, `dto/response` — Web-facing request/response DTOs.
- `common/exception` — `BusinessException` (carries an `HttpStatus`), `ResourceNotFoundException`, and `GlobalExceptionHandler` (`@RestControllerAdvice`) which maps all of these plus validation, auth, and access-denied errors to a uniform `ErrorResponse`.
- `common/dto` — `ApiResponse<T>` response envelope (`success`, `statusCode`, `message`, `data`, `timestamp`) used by every controller.
- `security` — JWT auth: `JwtTokenProvider`, `JwtAuthenticationFilter`, `JwtAuthenticationEntryPoint`, `CustomUserDetailsService`, `JwtProperties`.
- `config` — `SecurityConfig` (stateless JWT filter chain, public route matchers), `FlywayConfig`, `JpaAuditingConfig`, `OpenApiConfig`, `CloudinaryConfig`, `WebConfig`, `DevDataSeeder`.
- `generator` — `MigrationGenerator`, the entity-vs-DB diffing tool behind the `migrateDev` Gradle task.
- `helper` — Stateless utilities (`StringHelper`, `TokenHelper`).

When adding a new domain feature, follow the existing layering exactly: Controller → Mapper → single Command Model → Service interface + Impl → Validator (pure, entities passed in) → Repository → Entity. Don't inject repositories into validators, and don't give a service method more than one model parameter.

### Testing

Unit tests live under `src/test/java/com/hcmut/divvy/` mirroring the main package layout. The `validator` package has full coverage (one test class per validator, e.g. `UserValidatorTest`, `ExpenseValidatorTest`) — since validators take no repository dependency, tests instantiate them directly with `new` and real collaborators (e.g. a real `BCryptPasswordEncoder` instead of a mock) rather than mocking. Follow this pattern for new validators.

### Auth & security

- Stateless JWT auth (`SessionCreationPolicy.STATELESS`); `JwtAuthenticationFilter` runs before `UsernamePasswordAuthenticationFilter`.
- Public (no token) routes are listed explicitly in `SecurityConfig`: `/health`, `/api/health`, `/api/auth/**`, `/api/invitations/by-token`, `/api/groups/join-via-link/preview/**`, `/api/surveys/**`, `/actuator/**`, `/error`, Swagger/OpenAPI paths. Everything else requires `Authorization: Bearer <JWT>`.
- Get a token via `POST /api/auth/login`; see README for the full public/protected endpoint table.

### Config / environment

- Profiles: `dev` (default, enables `DevDataSeeder`) and `prod` (`application-dev.yml`, `application-prod.yml`, `application.yml`).
- Secrets/env vars are loaded via dotenv (`.env`, see `.env.example`): `JWT_SECRET`, Resend email vars (`RESEND_API_URL`, `RESEND_API_KEY`, `APP_MAIL_FROM`, `APP_MAIL_ENABLED`), Cloudinary vars (`CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET`), and `APP_BASE_URL` (used in emailed links, e.g. password reset).
- Email is sent via the Resend HTTP API (not SMTP); templates live in `src/main/resources/templates/email`.

## Documentation

- [ARCHITECTURE.md](./ARCHITECTURE.md) — architecture diagrams and a full request-flow walkthrough (Vietnamese).
- [docs/](./docs/) — per-module API specs (auth, user, group, group-member, invitation, expense, debt, settlement, activity, category, currency) plus an enum/endpoint reference (`docs/10-reference.md`) and business-logic docs (`docs/business/`).
