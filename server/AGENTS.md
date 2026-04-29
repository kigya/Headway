# AGENTS.md — Headway Server (Ktor + Microservices)

This document is for agents working on the **server** codebase.
For shared Kotlin conventions see the root [`AGENTS.md`](../AGENTS.md).

---

## Tech stack

| Area            | Stack                                                                      |
|-----------------|----------------------------------------------------------------------------|
| Language        | Kotlin 2.x, JVM 17                                                        |
| Framework       | Ktor + Netty                                                               |
| API             | KGraphQL (gateway), REST (internal microservices)                          |
| DI              | Koin                                                                       |
| Database        | Exposed (ORM), PostgreSQL                                                  |
| Serialization   | kotlinx.serialization                                                      |
| Build           | Gradle convention plugins (`build-logic/`)                                 |
| Runtime         | Docker (multi-stage: `gradle:8.14.0-jdk17` → `eclipse-temurin:17-jre`)     |
| Static analysis | Detekt + ktlint formatting                                                |

Hosted Postgres (Supabase) dashboards and project refs for agents and MCP: see [`.ai/cursor/RESOURCES.md`](../.ai/cursor/RESOURCES.md) (dev/prod).

---

## Commands

```bash
# Build all server services
cd server && ./gradlew build

# Build a specific service
cd server && ./gradlew :auth:internal:build
cd server && ./gradlew :database:internal:build
cd server && ./gradlew :home:internal:build
cd server && ./gradlew :gateway:build

# Create distribution archives
cd server && ./gradlew :auth:internal:installDist
cd server && ./gradlew :database:internal:installDist
cd server && ./gradlew :home:internal:installDist
cd server && ./gradlew :gateway:installDist

# Run Detekt (run after every task)
cd server && ./gradlew detekt

# Docker build
cd server && docker compose -f docker-compose.yml build

# Docker run
cd server && docker compose -f docker-compose.yml up
```

> **After completing any task the agent MUST run `./gradlew build` and `./gradlew detekt` to verify.**

---

## Project structure

```
server/
├── auth/
│   ├── api/          # Auth service contract: DTOs, Resources, URL holder
│   └── internal/     # Auth service implementation: routing, use cases, DI
├── database/
│   ├── api/          # Database service contract: DTOs, Resources, URL holder
│   ├── internal/     # Database service implementation: Exposed tables, repositories
│   └── migrations/   # SQL migrations for hosted Postgres (apply when schema columns are added)
├── home/
│   ├── api/          # Home service contract: DTOs, Resources, URL holder
│   └── internal/     # Home service implementation: routing, use cases, DI
├── gateway/          # GraphQL orchestrator — single client entry point
├── common/           # Shared utilities: ENV helpers, Ktor defaults, healthz, serializers
├── docker/           # env templates; per-ENV files e.g. env.gateway, env.auth, env.database, env.home
├── build-logic/      # Convention plugins: jvmLibrary, microserviceApplication, detekt
└── docker-compose.yml
```

---

## Architecture and module boundaries

### A microservice = two modules: `api` and `internal`
Each microservice follows this structure:

- `service-name/api`
    - The service **contract**: request/response models, type-safe resources (Ktor Resources), a URL holder (baseUrl), and other public types.
    - Contains no business logic or infrastructure code.
    - Used by other services and by the gateway (as a dependency) to **avoid duplicating models**.

- `service-name/internal`
    - The service **implementation**: DI module, use cases, repositories, mappers, Ktor routing/status pages, environment configuration, main/app.
    - Internal classes and implementation details should be `internal` by default.

Examples:
- `auth/api` + `auth/internal`
- `database/api` + `database/internal`
- `home/api` + `home/internal`
- `gateway` — a separate module (orchestrator), without an `api/internal` pair.

### Gateway is the single entry point for the client
- Client operations are implemented in `gateway` via GraphQL (KGraphQL + ktor plugin).
- Gateway contains:
    - public GraphQL models (`gateway/model`)
    - mappers between upstream DTOs and gateway models (`gateway/mapping`)
    - client repositories for services (`gateway/data/repository`)
    - orchestration/validation use cases (`gateway/domain/usecase`)
    - GraphQL error handling + extensions (`gateway/presentation/GatewayApi.kt`)

Downstream microservices expose only internal HTTP APIs under `/internal/v1/<service>` (via Ktor routing + resources).

---

## Packages, naming, visibility

### Namespaces
The base namespace is derived from the Gradle path and must start with:
- `dev.kigya.headway.<...>`

This is enforced in build-logic (`configureJvmLibrary`, `configureMicroserviceApplication`).

### Default visibility
- In `*/internal`: **`internal` by default** for classes, functions, DI modules, routing/status pages, use cases, contracts, mappers.
- In `*/api`: types are usually `public` (by default), because this is the contract.

Exceptions are allowed only when a type is truly needed outside. If not, keep it `internal` to avoid expanding the public surface.

You should follow the "internal by default" rule even if it is historically violated somewhere.

### Package structure conventions inside `internal`
Expected layout:

- `.../core` or `.../core/config`
    - `ConfigurationValues` (ENV vars + validateSecrets())
    - configs (e.g. `DatabaseConfig`, `JwtConfig`)
    - core extensions (e.g. `Database.dbQuery`)

- `.../di`
    - Koin module: `internal val <service>Module = module { ... }`

- `.../domain`
    - `usecase/*` — one use case per action
    - `repository/*Contract` — repository contracts
    - `error/*Exception` — domain exceptions (sealed class)

- `.../data`
    - `repository/*Repository` — repository implementations
    - integrations/infra (e.g. verifier, jwt, db tables)

- `.../mapping`
    - extension mappers (e.g. `ResultRow.toUser()`)

- `.../presentation`
    - `install<Service>Api(...)`
    - `plugin/*Routing`, `plugin/*StatusPages`
    - `routing/*` — routing split by domain (example: database/usersRouting + sessionRouting)

---

## Service contract (`api` module)

### Request/response models
- All DTOs for HTTP interaction live in `service/api/model`.
- Subpackage conventions:
    - `model/in` — incoming payloads (request body)
    - `model/out` — outgoing payloads (responses)
    - `model/resource` — Ktor Resources for type-safe routing

Requirements:
- DTOs that are serialized/deserialized by Ktor must be `@Serializable`.
- Fields that must match snake_case JSON are annotated with `@SerialName("...")`.
- Primitives like UUID/OffsetDateTime are serialized via shared serializers from `common/serialization` (example: `@Serializable(UUIDSerializer::class)`).

### Do not duplicate models
If service A uses types from service B — A adds a dependency on `B:api` and uses those types directly.

Example:
- `auth/api` uses `DatabaseSessionPlatform` and `DatabaseUser` from `database/api` (no platform/user duplicates in auth).

Rule: if a model already exists in a downstream `api` module, **do not create** an identical model in another `api` module or in the gateway. Exception: if you need a different "public" contract (as in the gateway), then create a gateway model and mappers.

### Type-safe resources (Ktor Resources)
- All paths are defined via `@Resource(...)`, and resources are grouped into classes/nested classes.
- Resources describe *relative* paths (e.g. `/google`, `session/validate`), while the service baseUrl is added at the Ktor routing level and/or via HttpClient basePath.

Examples:
- `AuthGoogleResource` has `@Resource("/google")`.
- `DatabaseResource` is rooted at `@Resource("")`, then `User`, `Invite`, `Google.Upsert`, `Session.Validate`, etc.
- On the server: `route(databaseServiceUrlHolder.baseUrl) { post<DatabaseResource.User.Invite> { ... } }`.
- On the client (gateway or another service): `httpClient.post(DatabaseResource.User.Invite()) { ... }`.

### ServiceUrlHolder and the typed Koin HttpClient "tag"
Each service declares:
- `data object <Service>KoinHttpClient : KoinHttpClient`
- `val <service>ServiceUrlHolder: ServiceUrlHolder<<Service>KoinHttpClient> = serviceUrlHolder("/internal/v1/<service>")`

Used for:
- DI (creating HttpClient with baseUrl/host/port)
- routing (routing by baseUrl on the service side)

---

## Service implementation (`internal` module)

### Ktor Application: API assembly template
Each service builds the application in the same way:

1) `defaultContentNegotiation()`
2) `defaultResources()`
3) `<service>StatusPages()`
4) `<service>Routing(...)`
5) Koin install + DI module
6) embeddedServer(Netty) in `main`

Example (`auth/internal`):
- `installAuthApi()` calls `defaultContentNegotiation()`, `defaultResources()`, `authStatusPages()`, `authRouting()`.

### Routing: structure and input validation
Rules:
- The root route is always `route(<service>ServiceUrlHolder.baseUrl)`.
- Inside it:
    - `healthzRouting()` — shared `/healthz`
    - domain routes (authByGoogle/refreshToken or usersRouting/sessionRouting)

Validation:
- In routing, only basic checks are performed: `trim()`, `isBlank()`, minimum format checks.
- Request errors in routing are thrown via Ktor exceptions (`BadRequestException`) or domain `*Exception.InvalidRequest` (if this is business validation).
- Responses: `call.respond(...)` with the correct HttpStatusCode.

Current style:
- `val x = body.field.trim()` and `if (x.isBlank()) throw BadRequestException("...")`.

### StatusPages: unified error handling scheme
Each service installs:
- `install(StatusPages) { handleDefaultExceptions(); handle<Service>Exceptions() }`.

`common/extension/StatusPagesConfigExtension.kt` provides base handlers for:
- BadRequestException, NotFoundException, MissingRequestParameterException, ParameterConversionException, CannotTransformContentToTypeException, UnsupportedMediaTypeException, PayloadTooLargeException, IllegalStateException, SecurityException, Throwable.

Then the service adds its own mapping from domain exceptions to HttpStatusCode:
- `InvalidRequest -> 400`
- `Unauthorized -> 401`
- `Forbidden/specializations -> 403`
- `Conflict -> 409` (in database)
- `DependencyUnavailable -> 503`
- `UpstreamProtocol -> 502` (in auth)

Rule: new domain exceptions must be added to `handle<Service>Exceptions()`.

### Domain exceptions: sealed class, "causes", and upstream semantics
Service exceptions are `sealed class <Service>Exception : RuntimeException`.
They include:
- message
- optional cause
- specializations for standard categories (InvalidRequest/Unauthorized/Forbidden/Conflict/DependencyUnavailable/UpstreamProtocol).

Rule:
- **DependencyUnavailable** is used when an external dependency is unavailable/crashed/timed out, etc.
- **UpstreamProtocol** is used when a dependency returns an unexpected status/protocol (does not match the contract).

### UseCase: one class = one action
A use case is created for each specific business action:
- LoginWithGoogleUseCase
- RefreshTokenUseCase
- InviteUserUseCase
- ValidateSessionUseCase
- etc.

Conventions:
- `internal class XUseCase(...) { suspend operator fun invoke(...) : ... }`
- Contains business validation, repository orchestration, and domain decisions (e.g., "user not active").
- Contains no HTTP code. HTTP belongs only to the routing layer.
- Contains no SQL/Exposed code. SQL/Exposed belongs only to repositories.

### Repository contracts and implementations
Conventions:
- In `domain/repository`: `internal interface <X>RepositoryContract`.
- In `data/repository`: `internal class <X>Repository(...) : <X>RepositoryContract`.

Service repositories (HTTP clients to other services):
- receive `HttpClient` via Koin (named qualifier via KoinHttpClient tag).
- use `ktor-client-resources` (e.g. `httpClient.post(DatabaseResource.Session.Validate())`).
- serialize request body via `setBody(dto)` + `contentType(ContentType.Application.Json)`.

DB repositories (database/internal):
- use `Exposed` + a wrapper `Database.dbQuery { ... }` for suspendTransaction and unified SQL error handling.
- tables live in `data/table/*Table`.
- mapping `ResultRow -> api model` lives in `mapping/*Mapper`.

---

## Inter-service communication (HTTP + type-safe resources)

### Creating an HttpClient for a service via `createServiceHttpClient`
Common mechanism in `common/extension/HttpClientConfig.kt`:

- `Module.createServiceHttpClient<ServiceKoinName>(host, port, baseUrl)`
    - registers `HttpClient(CIO)` in Koin with qualifier `named<ServiceKoinName>()`
    - sets basePath = `"/" + baseUrl.trim('/')`
    - `defaultRequest` constructs URLs as: `http://host:port/<baseUrl>/<resourcePath>`
    - normalizes double slashes

Important:
- baseUrl comes from `serviceUrlHolder.baseUrl` (e.g. `/internal/v1/auth`).
- resources (`@Resource`) are relative (`/google`, `session/validate`, etc.).

### Base Ktor client configuration
`baseConfig()` includes:
- ContentNegotiation JSON: `ignoreUnknownKeys = true`, `prettyPrint = false`
- Resources plugin
- HttpTimeout plugin

So clients are more tolerant to schema changes and do not fail on extra fields.

### Health checks semantics
- Each microservice has `/healthz` under its baseUrl (via `HealthzResource` + `healthzRouting()`).
- Gateway checks dependencies via probes:
    - BaseHttpProbe does GET `HealthzResource()` with 1s timeouts and interprets `isSuccess()`.

---

## Gateway: orchestration and public GraphQL contract

### Gateway layers
- `gateway/data/repository/*Repository` — HTTP clients to auth, database, home (e.g. `HomeRepository`)
- `gateway/domain/repository/*Contract` — contracts
- `gateway/domain/usecase/*UseCase` — business validation + repository invocation
- `gateway/mapping/*Mappers` — conversion upstream DTO <-> gateway models
- `gateway/model/*Models` — public GraphQL models
- `gateway/presentation/schema/*Schema` — GraphQL schema DSL
- `gateway/presentation/GatewayApi.kt` — GraphQL plugin install + errorHandler

### UpstreamCall and error mapping
All external calls from gateway use a single pattern:

- `upstreamCall(dependency, request, onSuccess)`
    - catches throwables at the HTTP call level => `GatewayException.DependencyUnavailable`
    - if status is successful => `onSuccess(response)`
    - otherwise => `response.toGatewayException(dependency)`

`toGatewayException` maps HttpStatusCode:
- 400 -> InvalidRequest
- 401 -> Unauthorized
- 403 -> Forbidden
- 404 -> NotFound
- 409 -> Conflict
- 503 -> DependencyUnavailable (with message "Service temporarily unavailable")
- else -> UpstreamProtocol (bad gateway)

Additionally:
- `safeBodyMessage()` reads `bodyAsText()`, normalizes whitespace, limits length to 2048.

Rule: new gateway repositories must use this pattern; direct "raw" `httpClient.get/post` calls without `upstreamCall` are not allowed.

### Public gateway models and mappers
Gateway models are serialized and used as GraphQL types:
- `GatewayUser`, `GatewayGoogleLoginResponse`, `GatewayRefreshAccessTokenResponse`
- enums: `GatewayUserRole`, `GatewayUserDepartment`, `GatewaySessionPlatform`, `GatewayServiceStatus`

Mappers:
- `AuthGoogleLoginResponse.toGateway()`
- `AuthRefreshAccessTokenResponse.toGateway()`
- `DatabaseUser.toGateway()`
- `GatewaySessionPlatform.toDatabase()` (platform translation for auth/api, which expects `DatabaseSessionPlatform`)

Rule: any contract mismatches between services and gateway are resolved **via mappers**, not by "tweaking" DTOs in api modules.

### Routes and GraphQL operation names
Operations and HTTP routes in gateway are fixed via sealed interfaces:
- `GatewayHttpRoute.GraphQL.path = "/api/v1/graphql"`
- `GatewayGraphqlOperation.*.name` — string operation names:
    - `_health`
    - `loginWithGoogle`
    - `refreshToken`
    - `inviteUser`

Rule: when adding a new GraphQL operation:
1) add a data object to `GatewayGraphqlOperation`
2) implement the use case
3) add a resolver to the corresponding `*Schema.kt`
4) register required type/enum/scalar in `GatewayApi.kt` schema

### GraphQL errorHandler and extensions
Gateway wraps errors into `GraphQLError` and adds extensions:
- `code` (enum name)
- `httpStatus`
- `dependency` (for DependencyUnavailable/UpstreamProtocol)
- `upstreamStatus` (for UpstreamProtocol)

Logging:
- INTERNAL and DEPENDENCY_UNAVAILABLE — `log.error(...)` with stacktrace
- others — `log.info(...)`

Rule: new error types in gateway must preserve this scheme (code + httpStatus + dependency details).

---

## Shared module `common`: required utilities and base conventions

### ENV utilities
- `stringEnv`, `intEnv`, `longEnv` require an environment variable (`requireNotNull(System.getenv)`), otherwise they fail immediately.
- Services keep their own `ConfigurationValues` and must:
    - have getters for HOST/PORT
    - have validateSecrets() and call it on application startup (especially in prod)

### Ktor server defaults
- `defaultContentNegotiation()`:
    - prettyPrint = true
    - encodeDefaults = true
- `defaultResources()` installs the Resources plugin

This must be enabled in each service (auth, database, home, …) and in gateway as needed (gateway uses the GraphQL plugin; but types/serializers still apply in KGraphQL).

### Healthz
- `HealthzResource` in `common/model/resource`
- `Route.healthzRouting()` adds GET `/healthz` and responds with "OK"

Rule: each microservice must have healthz under its baseUrl.

### Serializers
- `UUIDSerializer`
- `OffsetDateTimeSerializer`

Used in api DTOs to keep a stable wire format.

---

## Koin conventions

### Creating a Koin module
- Each service has `internal val <service>Module = module { ... }`.
- Registration:
    - `singleOf(::Impl) bind Contract::class` (preferred)
    - or `single { Impl(...) } bind Contract::class` (if named args/manual assembly is needed)

### Named qualifier for HttpClient
- HttpClient for a specific service is tagged as `named<<Service>KoinHttpClient>()`.
- Getting it in a repository:
    - `get(named<DatabaseKoinHttpClient>())`

### Use cases are registered as `singleOf(::XUseCase)`
- Use cases should not be factories unless necessary; current style is singleton.

---

## Build-logic and Gradle conventions (must be followed)

### Convention plugins
The project uses custom build-logic plugins:
- `libs.plugins.convention.base.jvmLibrary`
- `libs.plugins.convention.base.microserviceApplication`
- `libs.plugins.convention.component.serialization`
  etc.

Purpose:
- unify Kotlin/JVM + application configuration
- enable `-Xcontext-parameters` via `enableContextParameters()`
- unify group/namespace/archivesName

### `configureJvmLibrary { ... }`
Used in `*:api`, `common`, etc.
- sets `archivesName`, `version`
- sets `group` and `version` based on path rules
- archivesName is derived from the Gradle path by default (with `-` transformed to `.`/`-` as appropriate)

### `configureMicroserviceApplication { ... }`
Used in `*:internal` and `gateway`.
- requires `mainClass.set("...")`
- completes FQCN with the namespace if needed
- sets applicationDefaultJvmArgs, including `-Dio.ktor.development=...` (based on Gradle property `development`)

Rule: new microservices must use this mechanism instead of configuring the application manually.

### Dependency DSL
Semantic aliases are used:
- `microserviceDependencies { ... }`
- `jvmLibraryDependencies { ... }`
  and blocks:
- `libs { implementation(...) }`
- `projects { implementation(common); api(database.api) ... }`

Rule: keep this form; do not mix arbitrary `dependencies { ... }` with "custom" DSL without a reason.

### Detekt conventions
`internal.config.detekt.gradle.kts`:
- enables detekt + formatting plugins
- enables autocorrect via a system property
- has path-based exclusions

Rule: new modules must be compatible with detekt and formatting; do not introduce "quick hacks" without reasons.

---

## Docker / runtime

### Multi-stage Dockerfile
- Build stage on `gradle:8.14.0-jdk17`
- Builds installDist:
    - `database:internal:installDist`
    - `auth:internal:installDist`
    - `home:internal:installDist`
    - `gateway:installDist`
- Runtime images on `eclipse-temurin:17-jre`
- Each service is launched via `bin/<applicationName>`

### env files
- `docker-compose.yml` loads `docker/${ENV}/env.common` plus service-specific files (for example `env.gateway`, `env.auth`, `env.database`, `env.home`).

Rule: the service must start when required ENV vars are present. If ENV is missing, it should fail early (stringEnv/intEnv).

Important: secrets are validated in prod via `validateSecrets()`.

---

## Boundaries

### ✅ Always do
- run `./gradlew build` and `./gradlew detekt` after every task
- use the `api/internal` split for new microservices
- use type-safe Ktor Resources for all HTTP paths
- use `upstreamCall(...)` for all gateway-to-service calls
- use the shared `StatusPages` + service-specific exception handlers
- use convention plugins from `build-logic/`
- keep everything `internal` by default in `*/internal` modules
- validate input strings with `trim()` + `isBlank()` in routing
- register new domain exceptions in `handle<Service>Exceptions()`

### ⚠️ Ask first
- adding new Gradle dependencies
- modifying `build-logic/` convention plugins
- modifying Docker/env configuration
- modifying the `common` module public API surface
- database schema changes (Exposed tables)

### 🚫 Never do
- put business logic in routing (belongs in use cases)
- put HTTP/routing code in use cases (belongs in presentation layer)
- put SQL/Exposed code in use cases (belongs in repositories)
- duplicate models across `api` modules — reuse via dependency
- use string literals for HTTP paths — use `@Resource` classes
- make raw `httpClient.get/post` calls in gateway without `upstreamCall`
- commit secrets or ENV values to version control
- skip `healthz` for a new microservice

---

## Adding new functionality — checklists

### Add a new endpoint to a microservice
1) In `service/api`:
    - add DTO in `model/in` or `model/out` (if needed)
    - add `@Resource` in `model/resource`
    - if needed, add new serializers in `common` (rare)
2) In `service/internal`:
    - add a use case (one use case per action)
    - add/extend repository contract + implementation (if needed)
    - add routing (in `presentation/routing` or `presentation/plugin`)
    - add StatusPages mapping for new domain exceptions
3) DI:
    - register `singleOf(::NewUseCase)` and repositories
4) Do not duplicate models:
    - if a model already exists in another `api`, add the dependency and reuse it.

### Add a new GraphQL operation in gateway
1) `GatewayGraphqlOperation` — add a name.
2) `gateway/model`:
    - add a public model (if needed)
    - add enums/fields with `@SerialName` (GraphQL mapping)
3) `gateway/mapping`:
    - add mappers to/from upstream DTOs
4) `gateway/data/repository`:
    - add method in contract + implementation
    - call strictly via `upstreamCall(...)`
5) `gateway/domain/usecase`:
    - validate input (trim/isBlank/minimal checks)
    - call repository
6) `gateway/presentation/schema`:
    - add resolver in the appropriate schema file
7) `GatewayApi.kt` schema:
    - register type/enum/scalar if needed

---

## Small but mandatory style details

- Whenever reading strings from input — apply `trim()` before use.
- In services, the routing layer throws Ktor BadRequestException for empty/invalid fields.
- Dependency errors:
    - in microservices: `*Exception.DependencyUnavailable("dependency", cause=t)`
    - in gateway: `GatewayException.DependencyUnavailable(dependency=...)` and/or `UpstreamProtocol`.
- All microservice HTTP routes must be under `/internal/v1/<service>`.
- All microservices must have `/healthz` under their baseUrl.
- Inter-service calls must use Ktor Resources (type-safe) and the shared HttpClient with baseUrl via `createServiceHttpClient`.
- New wire (HTTP) models must live in the `api` module and be `@Serializable`.
- Only the gateway defines "public" client-facing models; microservices must not pull client requirements directly.

---

## Terms/definitions (for consistency)

**Microservice** — a standalone application (Netty + Ktor) with its own `internal` module, run separately, accessible via internal HTTP API.

**API module** — a microservice contract library: DTOs + Resources + baseUrl holder, without business logic.

**Gateway** — the orchestrator and the single client entry point; defines the GraphQL contract and aggregates/maps dependency errors.

**UseCase** — one class per business action; orchestrates repositories and makes domain decisions.

**RepositoryContract** — a port interface for data/infra access (HTTP, DB); implemented in `data`.

**Type-safe resources** — Ktor Resources classes which are the single source of truth for paths.
