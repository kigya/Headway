# Tech context

## Stack (from `AGENTS.md`)

- Kotlin 2.x, KMP
- Client: Compose Multiplatform (Android, Desktop, iOS, Web), MVIKotlin, Koin, Navigation3
- Server: Ktor + Netty, KGraphQL, Koin, Exposed, Docker
- Build: Gradle convention plugins (`build-logic/`), Detekt, ktlint, Compose lint

## Layout

- `client/` — application and feature modules
- `server/` — microservices and shared server libraries
- `build-logic/` — Gradle convention plugins

## Typical verification (from `.gitbook/general/quickstart.md` and feature skills)

- Client: `cd client && ./gradlew app:headwayAndroid:assembleDebug` and `cd client && ./gradlew detekt`
- Server: `cd server && ./gradlew build` and `cd server && ./gradlew detekt`

## Spec Kit (this repo)

- Initialized with **Specify CLI 0.4.0** (`specify init --here --ai cursor-agent --offline`). Config snapshot: `.ai/specify/init-options.json`
- Slash commands: `.ai/cursor/commands/speckit.*.md`
- Templates and scripts: `.ai/specify/templates/`, `.ai/specify/scripts/` (bash + PowerShell)

## Serena MCP (this repo)

- Install (official): `uv tool install -p 3.13 serena-agent@latest --prerelease=allow`; global init: `serena init`; update: `uv tool upgrade serena-agent --prerelease=allow`.
- Project: `.ai/serena/project.yml` (local; `.ai/serena/` is gitignored); languages include Kotlin plus TS/YAML/JSON/Markdown/Bash helpers for tooling/config files. Index once via `serena project index` after major structural changes.
- Cursor MCP: `.ai/cursor/mcp.json` runs `serena start-mcp-server --context ide --project ${workspaceFolder}` so the open workspace is the active project.
- Memory Bank remains authoritative: `base_modes` include `no-memories` and `no-onboarding`; `initial_prompt` reminds agents that policy lives in `AGENTS.md` / `.ai/cursor/rules/` and durable context in `.ai/cursor/memory-bank/`.

## Repomix (this repo)

- Repo config: `repomix.config.jsonc` (JSON with comments). Default output path `.repomix/repomix-output.xml`; directory `.repomix/` is gitignored.
- CLI snapshot from repo root: `npx -y repomix --output .repomix/repomix-output.xml`, or if `npx` cannot resolve the package: `npm exec --yes --package=repomix -- repomix --output .repomix/repomix-output.xml`. MCP mode uses `npx -y repomix --mcp` in `.ai/cursor/mcp.json`; switch to `npm exec --yes --package=repomix -- repomix --mcp` locally if needed.
- Uses `.gitignore`, Repomix defaults, and `ignore.customPatterns` for Gradle/Yarn stores, binaries, images, Repomix output, and similar noise.
