# System patterns

Patterns called out in `AGENTS.md` / `client/AGENTS.md` / `server/AGENTS.md` and reinforced in `.cursor/rules/`:

- **Policy by path:** use the **closest** `AGENTS.md` to edited files (root, `client/`, `server/`, design-system `AGENTS.md`).
- **Client UI:** Freud design system tokens and components — avoid raw Material3 or hard-coded theme values in features.
- **Client architecture:** MVIKotlin **Store** owns behavior; ViewModel stays a thin bridge to the store.
- **Compose lifecycle state:** `collectAsStateWithLifecycle()` on a ViewModel `StateFlow` already performs lifecycle-aware collection; do not add no-op `SideEffect` reads of the same state to “keep” the subscription (see `.cursor/rules/lessons-learned.mdc` §20).
- **Client errors:** model expected failures with **`Outcome`**, not ad-hoc exceptions for domain cases.
- **Client navigation:** use **`NavigatorContract`** / `NavigationIntent`, not ad-hoc platform navigation.
- **Server gateways:** wrap outbound HTTP in **`upstreamCall`** with consistent dependency error mapping.
- **kotlinx.serialization (server + client):** every `@Serializable` property uses `@SerialName` with the real persisted/wire key; polymorphic sealed variants annotate both discriminator and nested properties (see lessons-learned §18).
- **Server `data` layout:** `data/repository` holds only repository implementations; scope queries, sync helpers, and similar live under sibling `data/<role>/` packages (see lessons-learned §19).
- **Gateway health:** `CheckHealthStatusUseCase` probes each downstream service’s **`/healthz`** (under that service’s base URL) via `BaseHttpProbe`; add a probe for every Koin-tagged `HttpClient` the gateway uses for microservices (auth, database, home, …).
- **Platform adapters (Android, etc.):** inject `CoroutineDispatcher` / DI-owned `CoroutineScope` for IO and long-lived stores; cancel custom scopes on Koin `onClose`; avoid unused constructor fields; primary constructors list **`val`/`var` before plain parameters**; secure session storage implements **`Outcome`-returning** `SecureSessionStorageContract`, mapped to domain errors in `LocalSessionPersistenceRepository` (see lessons-learned §10 companion vs top-level const, §21, §29, §30).
- **Naming:** prefer full-word locals over `err` / `msg` / `idx` / `ex`-style abbreviations unless the short form is a standard wire/API token (see lessons-learned §31).
- **Client Gradle:** no empty `*MainDependencies { }` blocks in module `build.gradle.kts`; drop unused `extension.*MainDependencies` imports (see lessons-learned §32). Shared `implementation(project(…))` / `implementation(core.…)` used on **all** targets belongs in **`commonMainDependencies`**, not duplicated per platform (§33).
- **Thin typed values:** single-field wrappers (e.g. config URLs in DI) use **`@JvmInline value class`**, not `data class`, unless `copy`/data-class semantics are required (§34).
- **Lessons:** cumulative anti-patterns live in `.cursor/rules/lessons-learned.mdc` (curated, not a dump of every chat).

## Open questions

- Add feature-specific patterns here only when they are **stable** and **cross-cutting**; otherwise keep them in specs or task notes under `.specify/` / GitBook.
