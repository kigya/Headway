---
icon: mobile
---

# Client: conventions and workflow

Full canonical rules — [`client/AGENTS.md`](https://github.com/kigya/Headway/blob/trunk/client/AGENTS.md) in the repository. Below is a condensed guide for daily work.

## Stack

Kotlin Multiplatform, **Compose Multiplatform** (Android, iOS, Desktop, Web), **MVIKotlin**, **Koin**, **Navigation3**, errors via **`Outcome`** from `core:outcome`, UI via **Freud** design system (`core:design-system`). Static analysis: Detekt, ktlint, Compose lint.

## Verification commands

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
cd client && ./gradlew detekt
```

After meaningful client code changes, treat these steps as required before handing off a task.

## Repository layout (logical)

* `app/headway*` — platform entry points.
* `shared/` — app assembly: Koin, theme, `NavDisplay`.
* `core/design-system/` — Freud tokens and components (separate `AGENTS.md` inside the module).
* `core/outcome/` — typed success/failure.
* `feature/<name>/` — always three modules: **`api`**, **`di`**, **`internal`**.

## Feature: api / di / internal

* **`api`** — only `NavKey` (e.g. `@Serializable data object AuthScreenKey`) and route holder contract. No UI or business logic.
* **`di`** — Koin module: `factoryOf(StoreFactory)`, `viewModelOf(ViewModel)`, `singleOf(RouteHolder) bind Contract`.
* **`internal`** — screens, Store, ViewModel, themes, route holder implementations; everything **`internal`** by default.

Packages inside `internal`: `ui/route`, `ui/screen`, `ui/theme`.

## MVIKotlin

* **`Store`** interface with `Intent`, `State`, `Label`.
* Mark **`State`** with `@Immutable`, fields with default values.
* **`StoreFactory`** — `coroutineBootstrapper`, `coroutineExecutorFactory`, private `Action` / `Message`, **`Reducer`** with exhaustive `when` and no `else`.
* **`ViewModel`** — thin bridge: creates the store, `store.accept(Intent)`, exposes `stateFlow(viewModelScope)`. **No business logic in ViewModel.**

## Navigation

Only **`NavigatorContract`** and **`NavigationIntent`** (`NavigateTo`, `NavigateBack`, `ReplaceTopBy`). Do not call platform navigation APIs or string routes outside this contract.

New screen: key and contract in `api`, holder implementation, DI registration, **`entry` in `shared/App.kt`**, modules in `settings.gradle.kts`.

## Errors

Model expected failures with **`Outcome`** and sealed error types, not generic exceptions. Do not swallow `CancellationException`; handle `TimeoutCancellationException` per `client/AGENTS.md`.

## UI and Freud

* Feature theme: `internal object XTheme : FreudTheme()`, colors via `FreudColorScheme` extensions and **`FreudDynamicColor`**, palette from **`super.color.*`**, not raw `Color(0xFF…)`.
* Components and tokens: **`FreudText`**, **`FreudDsToken`**, design-system components. Raw Material3 in features without wrapping — not allowed.
* Screens: wrap with **`FreudFallback`**, adapt with **`FreudScreenByWidth`**, state via **`collectAsStateWithLifecycle()`**.
* Composables use **`modifier: Modifier = Modifier`**; attach modifier chains on the root layout node.
* Text parameter on `FreudText` is **`value`**, not `text`.

## Gradle

New modules use **convention plugins** from `client/build-logic`; do not duplicate Kotlin/Compose/Detekt setup by hand. Plugin list and roles — table in `client/AGENTS.md`.

## Coordinate before large changes

* new dependencies;
* `build-logic/` edits;
* public API of `core:design-system`;
* global navigation and `shared/App.kt`.

## New feature (short checklist)

1. `api`: `NavKey` + route holder contract.
2. `internal`: theme, Store + Factory, ViewModel, Screen, RouteHolder.
3. `di`: Koin module.
4. Wire module in `featureModules`, add `entry` in `App.kt`, `settings.gradle.kts`.
5. `assembleDebug` + `detekt`.
