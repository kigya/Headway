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

- Initialized with **Specify CLI 0.4.0** (`specify init --here --ai cursor-agent --offline`). Config snapshot: `.specify/init-options.json`
- Slash commands: `.cursor/commands/speckit.*.md`
- Templates and scripts: `.specify/templates/`, `.specify/scripts/` (bash + PowerShell)
