# AGENTS.md — Headway Shared Kotlin Conventions

Shared conventions for **all** Kotlin code in this monorepo (client + server).
Platform-specific rules live in nested files — follow the closest `AGENTS.md` to the code you are editing:
- **Client:** `client/AGENTS.md` (KMP, Compose, MVIKotlin, design system)
- **Server:** `server/AGENTS.md` (Ktor, microservices, Docker)
- **Design System:** `client/core/design-system/src/commonMain/kotlin/dev/kigya/headway/core/designSystem/AGENTS.md` (tokens, components, previews)

---

## Tech stack overview

| Area     | Stack                                                                                             |
|----------|---------------------------------------------------------------------------------------------------|
| Language | Kotlin 2.x, KMP                                                                                  |
| Client   | Compose Multiplatform (Android / Desktop / iOS / Web), MVIKotlin, Koin, Navigation3               |
| Server   | Ktor + Netty, KGraphQL, Koin, Exposed, Docker                                                    |
| Build    | Gradle convention plugins (`build-logic/`), Detekt + ktlint formatting + Compose lint             |

---

## Do

- use `internal` visibility by default; only expose what other modules truly need
- use `FreudDsToken<T>` for colors, typography, dimensions in client UI — never raw values
- use `Outcome<Error, Value>` for typed error handling in client — never raw exceptions
- use trailing commas in every multiline declaration and call site
- use named arguments when a call has 4+ arguments
- use `asSequence()` for 3+ chained collection operations
- use expression bodies for single-return functions
- default to small, focused files and diffs — avoid repo-wide rewrites unless asked
- keep functions under 6 parameters and constructors under 7
- keep cyclomatic complexity at 13 or below
- keep line length under 120; prefer class signatures ≤ 80 and function signatures ≤ 100

## Don't

- do not use wildcard imports (except `java.util.*` when absolutely necessary)
- do not leave `TODO`, `FIXME`, `STOPSHIP` comments or placeholder implementations (`TODO()`, `NotImplementedError`)
- do not use `print` / `println` in production code — use a logger
- do not use `lateinit` unless there is no cleaner option and the user explicitly requires it
- do not use `!!` unless there is a proven, tightly scoped invariant and no safe alternative
- do not call `System.exit`, `exitProcess`, `Thread.sleep` in suspend code, `printStackTrace`, `System.gc`
- do not hard-code colors, dimensions, or typography in feature modules — use the design system
- do not add new heavy dependencies without explicit approval
- do not use `var` for mutable collections (double mutability)
- do not use `else` in exhaustive `when` for sealed/enum/boolean subjects

---

## Naming conventions

| Kind                         | Convention         | Example                                         |
|------------------------------|--------------------|--------------------------------------------------|
| Classes, objects, enums      | `PascalCase`       | `AuthRepository`, `FreudTheme`, `ScreenState`    |
| Functions                    | `camelCase`        | `loadUserProfile()`, `mapToDomain()`             |
| Booleans                     | `is`/`has`/`are`/`can` prefix | `isEnabled`, `hasToken`, `canRetry`     |
| Constants                    | `UPPER_SNAKE_CASE` | `MAX_RETRY_COUNT`                                |
| Packages                     | lowercase, no `_`  | `dev.kigya.headway.feature.auth.internal`        |
| Compose: Unit-returning      | `PascalCase`       | `AuthScreen()`, `FreudButton()`                  |
| Compose: value-returning     | `camelCase`        | `rememberProfileState()`                         |

---

## Formatting contract

- 4-space indentation, no tabs, no semicolons
- no trailing whitespace, final newline required
- no consecutive blank lines, no blank line before `}`
- multiline signatures for 2+ parameters, with trailing commas
- chained calls: if wrapping, wrap the entire chain consistently
- multi-line `if`: always use braces
- annotations on their own line

---

## Null-safety

- prefer `as?` + `?:` over unsafe casts
- snapshot mutable nullable properties into a local `val` before null-checking
- do not use `?.` on non-null types
- use `==` / `!=` for strings, not `===` / `!==`
- use `map.getValue("key")` or `map.getOrDefault(...)` — never `map["key"]!!`

---

## Exceptions

- throw specific exception types with a message or cause — never generic `Exception`
- do not `catch` and immediately rethrow unchanged
- do not throw in `toString`, `hashCode`, `equals`, `finalize`
- use multiple `catch` blocks — do not `is`-check inside a single `catch`
- exception types must be `class`, not `object`

---

## Coroutines

- do not mark a function `suspend` unless it actually suspends
- functions returning `Flow` must not be `suspend`
- do not mix `suspend` with `CoroutineScope` receiver
- use `delay()` instead of `Thread.sleep` in coroutine code

---

## AI-assisted development

Agent-oriented **workflow** (Spec Kit slash commands, Memory Bank, skills) is documented in [`.ai/docs/ai-workflow.md`](.ai/docs/ai-workflow.md). **Code policy** for edits remains this file and the closest nested `AGENTS.md` by path; persistent **project context** for agents lives in [`.ai/cursor/memory-bank/`](.ai/cursor/memory-bank/).

---

## Self-checklist before finishing

- [ ] Every file has a correct `package` declaration
- [ ] Imports are explicit, ordered, no unused
- [ ] No `TODO`, `FIXME`, `println`, `lateinit`, `!!`, placeholder implementations
- [ ] Multiline signatures wrapped with trailing commas
- [ ] Booleans named with `is`/`has`/`are`/`can`
- [ ] Magic numbers extracted (except `-1`, `0`, `1`, `2`, `0.5`)
- [ ] `when` branches exhaustive without `else` for sealed/enum/boolean
- [ ] Exceptions are specific with messages
- [ ] No code comments (this project follows a "no comments" policy — KDoc for public APIs is allowed)
