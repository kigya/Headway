# System patterns

Patterns called out in `AGENTS.md` / `client/AGENTS.md` / `server/AGENTS.md` and reinforced in `.cursor/rules/`:

- **Policy by path:** use the **closest** `AGENTS.md` to edited files (root, `client/`, `server/`, design-system `AGENTS.md`).
- **Client UI:** Freud design system tokens and components — avoid raw Material3 or hard-coded theme values in features.
- **Client architecture:** MVIKotlin **Store** owns behavior; ViewModel stays a thin bridge to the store.
- **Client errors:** model expected failures with **`Outcome`**, not ad-hoc exceptions for domain cases.
- **Client navigation:** use **`NavigatorContract`** / `NavigationIntent`, not ad-hoc platform navigation.
- **Server gateways:** wrap outbound HTTP in **`upstreamCall`** with consistent dependency error mapping.
- **Gateway health:** `CheckHealthStatusUseCase` probes each downstream service’s **`/healthz`** (under that service’s base URL) via `BaseHttpProbe`; add a probe for every Koin-tagged `HttpClient` the gateway uses for microservices (auth, database, home, …).
- **Lessons:** cumulative anti-patterns live in `.cursor/rules/lessons-learned.mdc` (curated, not a dump of every chat).

## Open questions

- Add feature-specific patterns here only when they are **stable** and **cross-cutting**; otherwise keep them in specs or task notes under `.specify/` / GitBook.
