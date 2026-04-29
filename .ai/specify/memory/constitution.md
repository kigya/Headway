# Headway Engineering Constitution

This document is the constitutional source of truth for spec-driven work in the Headway monorepo. It translates the repository's `AGENTS.md`, path-specific `AGENTS.md` files, Cursor rules, and established system patterns into a single normative contract for planning, implementation, review, and verification.

The key words MUST, MUST NOT, REQUIRED, SHOULD, SHOULD NOT, and MAY are to be interpreted as described in RFC 2119.

## Core Principles

### I. Closest Source Of Truth
All work MUST start by identifying the paths it changes and loading the closest governing rules for those paths.

- Repository-wide Kotlin policy comes from the root `AGENTS.md`.
- `client/**` work MUST follow `client/AGENTS.md` in addition to the root rules.
- `server/**` work MUST follow `server/AGENTS.md` in addition to the root rules.
- `client/core/design-system/**` work MUST follow the design-system `AGENTS.md` in addition to the root and client rules.
- `.ai/cursor/rules/global.mdc` and `.ai/cursor/rules/lessons-learned.mdc` MUST be treated as always-applicable guidance.
- Client Kotlin work SHOULD also account for `.ai/cursor/rules/client.mdc` and `.ai/cursor/rules/detekt-guardrails.mdc`.
- Server Kotlin work SHOULD also account for `.ai/cursor/rules/server.mdc` and `.ai/cursor/rules/detekt-guardrails.mdc`.

Implications:

- Plans, specs, and tasks MUST name the concrete paths or modules they affect.
- Architectural decisions MUST be justified against the rules for those paths, not against generic Kotlin or framework advice.
- If guidance overlaps, the most specific path-scoped rule wins.
- Memory Bank context informs priorities and patterns, but MUST NOT override `AGENTS.md` or the rules.

### II. Explicit Architecture And Boundaries
Headway favors explicit module boundaries, small public surfaces, and clear placement of behavior.

- Visibility MUST default to `internal`; visibility MAY be widened only when another module genuinely requires it.
- Features and services MUST preserve the architecture already established in the repository instead of inventing parallel patterns.
- UI, business logic, network orchestration, database access, and public contracts MUST remain in their designated layers.
- Cross-module contracts MUST be reused rather than duplicated.
- New abstractions MUST solve a real boundary problem and MUST NOT be introduced only for organizational aesthetics.

This principle exists to keep client, server, and design-system code predictable, navigable, and cheap to evolve.

### III. Typed Contracts Over Ad-Hoc Behavior
The repository is opinionated about typed contracts at every boundary.

- Client state, navigation, and expected failures MUST be modeled with typed constructs.
- Server HTTP routes, DTOs, GraphQL models, and dependency failures MUST be expressed with explicit contract types and mappers.
- UI design decisions MUST flow through design-system tokens and semantic themes, not raw implementation values.
- Serialization and API surface decisions MUST be explicit and stable.

This principle means the codebase prefers `Outcome`, `NavKey`, `NavigationIntent`, `@Resource`, `@Serializable`, `@SerialName`, gateway mappers, and `FreudDsToken` over strings, raw exceptions, ad-hoc route literals, or inline hard-coded visuals.

### IV. Convention Plugins And Shared Infrastructure First
Build, DI, HTTP client setup, health probing, and theme infrastructure MUST use the repository's shared mechanisms before custom alternatives are considered.

- Gradle modules MUST use the existing convention plugins under `build-logic/`.
- Shared Ktor, Koin, design-system, and Compose infrastructure MUST be reused.
- New code MUST integrate with the repository's existing verification, serialization, health-check, and error-mapping infrastructure.
- Heavy dependencies MUST NOT be added without explicit approval.

This principle prevents configuration drift and preserves consistent behavior across platforms and services.

### V. Verification Is Part Of The Definition Of Done
No task is complete until the appropriate verification steps have been run for the paths changed, unless the user explicitly waives verification or the environment makes it impossible.

- Client changes MUST be verified with `cd client && ./gradlew app:headwayAndroid:assembleDebug` and `cd client && ./gradlew detekt`.
- Server changes MUST be verified with `cd server && ./gradlew build` and `cd server && ./gradlew detekt`.
- Detekt failures are blocking; the repository treats static analysis as a quality gate, not as optional advice.
- Tests SHOULD be added or updated when they materially reduce regression risk, when the user asks, or when nearby coverage patterns make the absence meaningful.
- Broad or low-value tests that merely restate the implementation SHOULD be avoided.

### VI. Simplicity, Safety, And Change Discipline
The repository values focused diffs, reversible decisions, and clear reasons for complexity.

- New complexity MUST be justified in the feature plan when a simpler alternative is rejected.
- Existing patterns SHOULD be preferred over novel abstractions.
- Secrets, environment values, and credentials MUST NOT be committed.
- Destructive or irreversible repository actions MUST NOT be taken without explicit approval.
- Placeholder implementations, silent shortcuts, and speculative rewrites MUST NOT be introduced.

## Engineering Directives

### Shared Kotlin Directives
These directives apply across client and server Kotlin code unless a path-specific rule is stricter.

- Use explicit imports; wildcard imports MUST NOT be used except `java.util.*` when truly necessary.
- Multiline declarations and call sites MUST use trailing commas.
- Named arguments SHOULD be used for calls with four or more arguments.
- `asSequence()` SHOULD be used for three or more chained collection operations.
- Expression bodies SHOULD be preferred for single-return functions.
- Line length SHOULD remain under 120.
- Functions SHOULD have at most six parameters; constructors SHOULD have at most seven.
- Cyclomatic complexity SHOULD stay at or below 13.
- Exhaustive `when` expressions on sealed types, enums, and booleans MUST NOT use `else`.
- `TODO`, `FIXME`, `STOPSHIP`, `TODO()`, `NotImplementedError`, `println`, `print`, and `printStackTrace` MUST NOT appear in production code.
- `!!` MUST NOT be used without a tightly proven invariant and no safer alternative.
- `lateinit` MUST NOT be used unless there is no cleaner option and the requirement is explicit.
- Functions that compute or map values SHOULD be named with verbs or `toTargetType` style conversions.
- File-level `private const val` declarations SHOULD be placed at the bottom of the file.
- Comments explaining code flow SHOULD NOT be added; KDoc is allowed for public APIs only.

### Client Directives
The client is a Kotlin Multiplatform Compose application with strict feature boundaries and MVIKotlin state management.

#### Client module structure

- New client features MUST use `feature/<name>/{api,di,internal}`.
- `api` MUST contain only public contracts such as `NavKey` and route-holder contracts.
- `di` MUST assemble Koin registrations.
- `internal` MUST contain screens, stores, view models, route holders, and themes.
- Feature implementation details SHOULD remain `internal`.

#### Client state and behavior

- MVIKotlin `Store` owns behavior; `ViewModel` MUST remain a thin bridge.
- `Store` MUST define `Intent`, `State`, and `Label`.
- `State` SHOULD be `@Immutable` and SHOULD provide default values for fields.
- StoreFactory internals SHOULD use private `Action` and `Message` sealed interfaces where applicable.
- ViewModel public methods MUST dispatch intents through `store.accept(...)`.
- Business logic, branching workflow decisions, navigation policy, and state mutation logic MUST NOT live in `ViewModel`.
- `collectAsStateWithLifecycle()` already provides the subscription; no-op `SideEffect` reads to keep collection alive MUST NOT be used.

#### Client UI and design system

- Feature UI MUST use the Freud design system, not raw Material3 or raw theme values.
- Hard-coded colors, typography, spacing, and raw `Color` literals MUST NOT appear in feature UI.
- Screens SHOULD use `FreudFallback` and `FreudScreenByWidth` where the established screen pattern expects them.
- Composables that emit UI SHOULD expose a single `modifier: Modifier = Modifier`, applied to the root layout node.
- `FreudText` MUST use the `value` parameter name, not `text`.
- Boolean names SHOULD follow `isXxx`, `hasXxx`, `areXxx`, `canXxx`, or `shouldXxx`.
- Android-only APIs MUST NOT be used in `commonMain`.

#### Client navigation and errors

- Navigation MUST go through `NavigatorContract` and `NavigationIntent`.
- `NavKey` definitions MUST be serializable and stable.
- Expected domain failures MUST be modeled with `Outcome`, not ad-hoc exceptions.
- `CancellationException` MUST be rethrown.
- `TimeoutCancellationException` MUST be handled according to the established `Outcome` wrappers rather than used as unchecked control flow.

#### Compose stability

- `@Immutable` models used in composition MUST use `ImmutableList`, `ImmutableMap`, or `ImmutableSet` for collection fields.
- Stable collection wrappers MUST also contain stable element types.

### Design-System Directives
The design-system module is the source of truth for reusable UI primitives and tokens.

- The module MUST remain reusable and MUST NOT absorb feature-specific business logic.
- Public component APIs SHOULD accept `FreudDsToken<T>` for theme-driven colors, text styles, dimensions, and shapes.
- Raw values MAY be resolved internally, but only as late as possible.
- Dynamic token resolution SHOULD use the existing `provides` pattern and semantic theme mappings.
- Helper objects, preview utilities, and construction glue SHOULD remain `internal` or `private`.
- Components SHOULD keep internal defaults in a `private object XxxDefaults` unless a public default surface is intentionally required.
- Public APIs SHOULD avoid leaking Material types unless they are truly part of the long-term contract.
- Preview files SHOULD live under `component/preview` and SHOULD validate light and dark themes, key variants, and meaningful states.
- Design-system spacing, shapes, and typography SHOULD be tokenized rather than expressed as raw literals.
- Non-scaled typography behavior is currently a deliberate policy; any change to it MUST be treated as an API and design decision, not as an incidental tweak.

### Server Directives
The server is a multi-service Kotlin/JVM system built around Ktor microservices, a GraphQL gateway, Exposed, and shared infrastructure.

#### Server module structure

- Each microservice MUST preserve the `api` and `internal` split.
- `api` modules define contracts only: DTOs, `@Resource` routes, URL holders, and other shared types.
- `internal` modules define implementation only: routing, use cases, DI, repositories, configuration, mapping, and runtime assembly.
- `gateway` is the single client-facing entry point and owns the public GraphQL contract.
- Types inside `*/internal` MUST default to `internal`.
- DTOs MUST NOT be duplicated across service `api` modules; owning `api` modules MUST be reused as dependencies.

#### Server routing and layering

- All HTTP paths MUST be defined with Ktor `@Resource` classes; raw string route literals are not allowed for contract paths.
- All microservice routes MUST live under `/internal/v1/<service>`.
- Every microservice MUST expose `/healthz` under its base URL.
- Routing MUST remain thin and perform only basic validation such as `trim()`, `isBlank()`, and minimal format checks.
- Business rules MUST live in use cases.
- SQL and Exposed code MUST live in repositories or database helpers, not in use cases.
- HTTP types and routing concerns MUST NOT leak into use cases.

#### Server serialization and contracts

- Wire DTOs MUST be `@Serializable`.
- Serializable properties SHOULD be annotated with `@SerialName` using the actual wire key.
- Shared serializers such as UUID and time serializers SHOULD be reused from `common`.
- Gateway public models MUST remain separate from upstream service DTOs when contracts differ.
- Contract mismatches MUST be resolved with mappers, not by ad-hoc mutation of upstream `api` types.

#### Server dependency handling

- Gateway outbound calls MUST use `upstreamCall(...)`.
- Non-success upstream responses MUST be translated with the established gateway exception mapping.
- New gateway repositories MUST NOT make raw `httpClient.get` or `httpClient.post` calls without `upstreamCall(...)`.
- Downstream microservices called through dedicated Koin `HttpClient`s MUST have matching health probes wired into aggregated gateway health checks.

#### Server error modeling

- Service-specific domain exceptions SHOULD be sealed classes with explicit categories.
- `DependencyUnavailable` MUST be used for unavailable or failing dependencies.
- `UpstreamProtocol` MUST be used for unexpected upstream protocol or contract behavior.
- New domain exceptions MUST be registered in the service's `StatusPages` handling.
- Database-level expected business-rule rejections SHOULD be mapped to invalid-request style exceptions rather than collapsed into dependency outages.

#### Server configuration and runtime

- Environment behavior SHOULD branch through typed environment helpers rather than raw string comparisons.
- Required environment values MUST fail fast when absent.
- Secret validation MUST remain enforced in production paths.
- Docker and runtime configuration changes are architectural changes and require explicit review.

## Project Context And Approved Technical Baseline

### Language And Platforms

- Primary language: Kotlin 2.x.
- Client platforms: Android, Desktop, iOS, and Web through Compose Multiplatform.
- Server runtime: JVM 17 with Ktor and Netty.

### Primary Frameworks And Infrastructure

- Client: Compose Multiplatform, MVIKotlin, Koin, Navigation3, Freud design system, `Outcome`.
- Server: Ktor, KGraphQL, Koin, Exposed, PostgreSQL, Docker.
- Build: repository convention plugins under `build-logic/`.
- Static analysis: Detekt everywhere, plus ktlint formatting and Compose lint on the client.

### Repository Layout Expectations

- `client/` contains application entries, shared composition roots, feature modules, navigation, DI, and design-system code.
- `server/` contains microservices, the gateway, common infrastructure, migrations, Docker assets, and server build logic.
- `.ai/specify/` contains spec, plan, task, checklist, and agent templates for spec-driven delivery.
- `.ai/cursor/memory-bank/` captures current focus and stable cross-cutting patterns; it informs but does not supersede this constitution.

## Development Workflow And Quality Gates

### Constitution Check For Every Plan
Every implementation plan MUST pass the following constitutional gate before research is considered complete, and MUST be re-checked after design decisions are made.

1. The affected paths and governing rule sources are explicitly listed.
2. The planned module structure matches repository conventions for those paths.
3. The location of behavior is correct:
   client Store vs ViewModel, server routing vs use case vs repository, design-system vs feature.
4. Public and wire contracts are typed and explicit:
   `NavKey`, `NavigationIntent`, `Outcome`, `@Resource`, `@Serializable`, `@SerialName`, mappers, probes, token APIs.
5. The plan does not introduce prohibited shortcuts:
   raw Material3 in feature UI, raw gateway HTTP calls, duplicated DTOs, string route literals, secret commits, placeholder implementations.
6. The verification commands for the changed areas are named explicitly.
7. Any required approval points are called out:
   new dependencies, build-logic changes, navigation rewiring, design-system public API changes, Docker or env changes, schema changes.

### Implementation Expectations

- Prefer focused diffs over repository-wide rewrites.
- Reuse established patterns before introducing new abstractions.
- Keep build scripts minimal and convention-plugin driven.
- Do not bypass team Detekt configuration with ad-hoc overrides unless the change itself is an approved build-policy update.
- Do not silently widen public API surfaces.

### Testing And Validation Expectations

- Verification commands are mandatory quality gates, not optional housekeeping.
- Add or update tests when they are the clearest way to protect behavior.
- Avoid low-signal tests that add noise without meaningful regression protection.
- If verification cannot be run, the blocker and the unverified risk MUST be recorded in the task result or review notes.

## Exceptions, Complexity, And Amendments

### Exception Policy
Violations of this constitution are allowed only when they are explicit, necessary, and documented.

- Any deliberate violation MUST be recorded in the plan's Complexity Tracking section.
- The plan MUST name the violated rule, why it is necessary, and which simpler alternative was rejected.
- Temporary exceptions SHOULD include a removal condition or migration path.
- Convenience, time pressure, or personal preference are not sufficient justifications.

### Amendment Policy
This constitution MAY be amended when repository rules, architecture, or platform strategy changes materially.

- Amendments MUST preserve alignment with the root `AGENTS.md`, path-specific `AGENTS.md` files, and active repository rules.
- Amendments SHOULD be made together with any related rule or architecture updates so planning guidance and implementation guidance do not diverge.
- Changes that alter public APIs, shared infrastructure, or policy-heavy behavior SHOULD be treated as versioned decisions.

## Governance

This constitution supersedes default template assumptions in `.ai/specify/` and is the required basis for `spec`, `plan`, `tasks`, and review quality gates in this repository.

Governance rules:

- All implementation plans MUST include a Constitution Check derived from this file.
- Reviewers and agents MUST reject work that violates mandatory principles without documented exceptions.
- The closest path-specific rule set remains authoritative during implementation.
- Memory Bank entries describe active context and stable patterns, but they do not grant permission to violate constitutional rules.
- Lessons captured in `.ai/cursor/rules/lessons-learned.mdc` are considered cumulative anti-pattern guidance and SHOULD inform future planning and review.

**Version**: 1.0.0 | **Ratified**: 2026-04-08 | **Last Amended**: 2026-04-08
