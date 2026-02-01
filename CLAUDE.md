# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build              # Build the entire project
./gradlew :app:run           # Run the Ktor server (port 8080)
./gradlew test               # Run all tests
./gradlew :module:test       # Run tests for a specific module (e.g., :data:test)
./gradlew clean build        # Clean and rebuild
```

## Project Purpose
Portfolio project targeting Berlin/Germany entry-level+ software engineering roles.
Demonstrates: Clean architecture, SOLID principles, multi-module design,
API integration, data visualization, and full-stack development.

## Architecture Philosophy
**Multi-module Gradle project following Clean Architecture + SOLID principles**

This structure enables:
- Clear separation of concerns
- Domain-driven design
- Independent testing of business logic
- Future scalability (modules → microservices if needed)
- Dependency inversion (domain defines contracts, data implements)

## Architecture

This is a Kotlin/Ktor multi-module project following Clean Architecture principles for a birdwatching analytics platform that integrates with the eBird API.

### Module Structure

```
app/           → Ktor server entry point, routing, DI setup
domain/        → Business domain models, repository interfaces, DomainResult<T>
data/          → Repository implementations, DTOs, DTO-to-domain mappers
core/          → Cross-cutting concerns (HttpClientFactory)
database/      → Database infrastructure (prepared for future use)
shared/        → Shared utilities (prepared for future use)
features/      → Feature modules (api_explorer, analytics, predictions)
```

### Module Dependencies

- `app` depends on `core`, `data`, and `domain`
- `data` depends on `core` and `domain`
- `domain` has no internal module dependencies (uses kotlinx-serialization only — needed so Ktor can serialize domain models in route responses)

### Key Patterns

- **Repository Pattern**: Interfaces in `domain/`, implementations in `data/`. Interfaces are split by ISP — e.g. `RegionObservationRepository` and `NearbyObservationRepository` rather than one monolithic interface. A single impl class can implement multiple interfaces; the same instance is injected for each.
- **DTO Mapping**: DTOs with `@Serializable` in `data/` layer, `.toDomain()` extension functions for mapping to domain models. DTOs keep the raw API shape (e.g. `observationDate` as `String`); the mapper converts to proper types.
- **Manual DI**: Constructor injection configured in `Application.kt`. Repository interfaces are passed into route extension functions as parameters.
- **Sealed Result Type**: `DomainResult<T>` (Success/Failure) in `domain/` for type-safe error handling. Lives in `domain` — not `core` — because repository interfaces (also in domain) return it, and domain cannot depend on core.
- **Route Extensions**: Each route file exports a `fun Route.xxxRoutes(...)` extension. Use `Route` as the receiver, not `Routing`. In Ktor 3.x, the `routing { }` block gives a `Route` receiver in both production (`Application.routing`) and test (`testApplication`) contexts. Using `Routing` compiles in production but fails in tests.
- **Date Serialization**: eBird returns dates as `"yyyy-MM-dd HH:mm"`. A custom `LocalDateTimeSerializer` (KSerializer<LocalDateTime>) in `domain/` handles this. Applied per-field: `@Serializable(with = LocalDateTimeSerializer::class)`.

### Testing Patterns

- **Repository tests**: Use `HttpClient(MockEngine(...))` with `install(ContentNegotiation) { json() }` in the client config block. ContentNegotiation is required — without it `response.body<T>()` throws during deserialization, which gets swallowed by the catch block and surfaces as a confusing Failure.
- **Route tests**: Use `testApplication { install(ContentNegotiation) { json() }; routing { ... } }`. Configure directly inside the `testApplication` block — do not wrap in `app { }` or `application { }`. Mock repositories with `mockk` and `coEvery`. The block receiver is `ApplicationTestBuilder` (not `TestApplicationBuilder`) — this is the subclass that has the `client` property. If extracting a helper, type the block as `suspend ApplicationTestBuilder.() -> Unit`. Also need `import io.ktor.client.request.*` for `HttpClient.get()` and `import io.ktor.client.statement.*` for `bodyAsText()`.

### Configuration

- Environment variables loaded from `.env` file (dotenv-kotlin) or system environment
- `EBIRD_API_KEY` required for eBird API authentication
- Application config in `app/src/main/resources/application.yaml`

### Tech Stack

- Kotlin 2.3.0, JVM 21
- Ktor 3.4.0 (Netty server, CIO client)
- Kotlinx Serialization for JSON
- Gradle 9.3.0
