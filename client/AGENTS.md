# AGENTS.md — Headway Client (KMP + Compose Multiplatform)

This document is for agents working on the **client** codebase.
For shared Kotlin conventions see the root [`AGENTS.md`](../AGENTS.md).
For the design system see [`core/design-system/.../AGENTS.md`](core/design-system/src/commonMain/kotlin/dev/kigya/headway/core/designSystem/AGENTS.md).

---

## Tech stack

| Area            | Stack                                                                                   |
|-----------------|-----------------------------------------------------------------------------------------|
| Language        | Kotlin 2.x, KMP (commonMain + androidMain / iosMain / desktopMain / wasmJsMain)         |
| UI              | Compose Multiplatform (Android / Desktop / iOS / Web)                                   |
| State           | MVIKotlin (Store + Intent / State / Label / Action / Message)                           |
| DI              | Koin Multiplatform, `KoinMultiplatformApplication`                                      |
| Navigation      | Navigation3 (`NavDisplay`, `NavKey`, `entryProvider`)                                   |
| Error handling  | `Outcome<Error, Value>` (`core:outcome`)                                                |
| Design system   | `core:design-system` — `FreudTheme`, `FreudDsToken<T>`, `FreudColorScheme`              |
| Static analysis | Detekt + ktlint formatting + Compose lint (Lopez + Kode plugins)                        |
| Build           | Gradle convention plugins (`build-logic/`): `base.*`, `component.*`, `internal.config.*` |

---

## Commands

```bash
# Android debug build (primary verification step — run after every task)
cd client && ./gradlew app:headwayAndroid:assembleDebug

# Detekt (run after every task)
cd client && ./gradlew detekt

# Build a single module
cd client && ./gradlew :feature:auth:internal:compileKotlinAndroid

# Full project build (when explicitly requested)
cd client && ./gradlew build
```

> **After completing any task the agent MUST run the Android build and Detekt to verify.**

---

## Project structure

```
client/
├── app/
│   ├── headwayAndroid/         # Android application entry point
│   ├── headwayDesktop/         # Desktop (JVM) application entry point
│   ├── headwayIOS/             # iOS application entry point (via shared)
│   └── headwayWeb/             # WasmJs application entry point
├── shared/                     # App.kt — KoinMultiplatformApplication + FreudTheme + NavDisplay
├── core/
│   ├── annotation/             # @MarkerInterface and other shared annotations
│   ├── design-system/          # FreudTheme, tokens, components, previews (see dedicated AGENTS.md)
│   └── outcome/                # Outcome<Error, Value> — typed error handling
├── feature/
│   ├── auth/                   # api/ + di/ + internal/
│   └── splash/                 # api/ + di/ + internal/
├── navigation/
│   ├── api/                    # NavigatorContract, NavigationIntent, ScreenRouteHolderContract
│   ├── di/                     # Navigation Koin module
│   └── internal/               # HeadwayNavigator implementation
├── di/
│   ├── api/                    # DispatcherKey
│   └── modules/                # appModules, featureModules, navigationModules, dispatcherModule
└── build-logic/
    ├── base/                   # Convention plugins: androidApplication, desktopApplication, sharedLibrary, detekt
    ├── component/              # Convention plugins: compose, composeNavigation, koin, mvi, room, serialization
    └── gradle-extension/       # Shared Gradle extensions
```

---

## Feature module architecture

Every feature follows a strict **api / di / internal** split:

### `feature/<name>/api`
- `@Serializable data object <Name>ScreenKey : NavKey` — navigation key
- `interface <Name>ScreenRouteHolderContract : ScreenRouteHolderContract` — route contract
- Contains **no** business logic or UI code

### `feature/<name>/di`
- Koin module: `val <name>Module get() = module { ... }`
- Registers: `factoryOf(::StoreFactory)`, `viewModelOf(::ViewModel)`, `singleOf(::RouteHolder) bind Contract::class`

### `feature/<name>/internal`
- All implementation details — screens, stores, view models, themes, route holders
- Everything is `internal` by default

Package structure inside `internal`:

```
internal/ui/
├── route/      # ScreenRouteHolder implementations
├── screen/     # Screen composables, Store interfaces, StoreFactory classes, ViewModels
└── theme/      # Feature-specific theme objects
```

---

## MVIKotlin Store pattern

Every screen's state management follows this structure:

### 1. Store interface
```kotlin
interface AuthStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object OpenNoAccess : Intent
    }

    sealed interface Label

    @Immutable
    data class State(val shouldDisplayText: Boolean = false)
}
```

Rules:
- `State` is annotated with `@Immutable`
- `Intent` = user actions, `Label` = one-shot side effects, `State` = UI state
- `State` fields use default values

### 2. StoreFactory
```kotlin
class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
) {
    fun create(executorCoroutineScope: CoroutineScope): AuthStore = object :
        AuthStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onIntent<Intent.OpenNoAccess> {
                    navigator.navigate(NavigationIntent.NavigateTo(AuthNoAccessScreenKey))
                }
            },
            reducer = Reducer { message ->
                when (message) {
                    Message.ShowText -> copy(shouldDisplayText = true)
                }
            },
        ) {}

    private sealed interface Action
    private sealed interface Message
}
```

Rules:
- `Action` = internal actions dispatched by bootstrapper, `Message` = state mutations
- `Action` and `Message` are `private sealed interface` inside StoreFactory
- Use `coroutineBootstrapper` and `coroutineExecutorFactory`
- Exhaustive `when` in reducer — no `else`
- Navigation calls go through `NavigatorContract`, never directly

### 3. ViewModel
```kotlin
class AuthViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
) : ViewModel() {
    private val store: AuthStore = AuthStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
    ).create(viewModelScope)

    val uiState: StateFlow<AuthStore.State> = store.stateFlow(viewModelScope)

    fun onOpenNoAccess() {
        store.accept(AuthStore.Intent.OpenNoAccess)
    }
}
```

Rules:
- ViewModel is a thin bridge — creates the Store and exposes `uiState` via `stateFlow(viewModelScope)`
- Public methods dispatch intents via `store.accept(...)`
- No business logic in ViewModel

---

## Screen composables

```kotlin
@Composable
internal fun AuthScreen() {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContent(
        state = state,
        onOpenNoAccess = viewModel::onOpenNoAccess,
    )
}

@Composable
private fun AuthScreenContent(
    state: State<AuthStore.State>,
    onOpenNoAccess: () -> Unit,
) {
    val content: @Composable BoxScope.() -> Unit = {
        FreudFallback(isError = ..., onRetry = { ... }) {
            Column(...) { ... }
        }
    }
    FreudScreenByWidth(narrow = content, wide = content)
}
```

Rules:
- Top-level screen composable is `internal`, calls `koinViewModel<VM>()`
- Content is extracted into a `private` stateless composable accepting `State<T>` + callbacks
- Wrap content in `FreudFallback` for error/network handling
- Wrap in `FreudScreenByWidth(narrow = ..., wide = ...)` for adaptive layout
- Use `collectAsStateWithLifecycle()` for state collection

---

## Feature themes

Each feature defines its own theme as an `internal object` extending `FreudTheme()`:

```kotlin
internal object AuthTheme : FreudTheme() {
    val FreudColorScheme.authBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.green40,
            dark = super.color.green90,
        )

    val FreudColorScheme.brandTextColor
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown30,
        )
}
```

Rules:
- Theme object is `internal` — never exposed outside the feature module
- Colors are `FreudColorScheme` extension properties with `@Composable get()`
- Use `this provides FreudDynamicColor(light = ..., dark = ...)` for theme-aware colors
- Access palette via `super.color.*` — never raw Color values
- Consume in screen: `AuthTheme.colorScheme.authBackground`, `AuthTheme.typography.headingSmExtraBold`

---

## Navigation

Navigation uses **Navigation3** (`androidx.navigation3`):

- `NavKey` — serializable navigation keys (`@Serializable data object AuthScreenKey : NavKey`)
- `NavigationIntent` — sealed interface: `NavigateBack`, `NavigateTo(key)`, `ReplaceTopBy(key, asyncRunner)`
- `NavigatorContract` — interface with `backStack` and `navigate(intent)`
- `ScreenRouteHolderContract` — interface: `screenNavigationKey: NavKey` + `content: @Composable () -> Unit`

Route holder implementation:
```kotlin
class AuthScreenRouteHolder : AuthScreenRouteHolderContract {
    override val screenNavigationKey = AuthScreenKey
    override val content = @Composable { AuthScreen() }
}
```

App entry point (`shared/App.kt`) assembles routes via `NavDisplay` + `entryProvider`.

---

## DI (Koin)

### Feature module registration
```kotlin
val authModule
    get() = module {
        factoryOf(::AuthStoreFactory)
        viewModelOf(::AuthViewModel)
        singleOf(::AuthScreenRouteHolder) bind AuthScreenRouteHolderContract::class
    }
```

Rules:
- `StoreFactory` is registered via `factoryOf` (new instance per injection)
- `ViewModel` is registered via `viewModelOf`
- `RouteHolder` is registered via `singleOf` with `bind Contract::class`
- Feature modules are aggregated in `di/modules` → `featureModules` list
- App entry uses `KoinMultiplatformApplication(config = KoinConfiguration { modules(appModules) })`

---

## Outcome — typed error handling

Use `Outcome<Error, Value>` from `core:outcome` instead of raw exceptions:

```kotlin
sealed interface Outcome<out Error, out Value> {
    data class Success<Value>(val value: Value) : Outcome<Nothing, Value>
    data class Failure<Error>(val error: Error) : Outcome<Error, Nothing>
}
```

Key extensions: `getOrNull()`, `getOrElse {}`, `handle(onFailure, onSuccess)`, `unwrap(onFailure, onSuccess)`, `mapSuccess {}`, `mapFailure {}`, `onSuccess {}`, `onFailure {}`.

Suspend-safe wrappers: `outcomeCatching {}`, `outcomeSuspendCatching {}`, `outcomeSuspendCatchingOn(dispatcher) {}`.

Rules:
- Define domain errors as `sealed interface` — never use raw exceptions for expected failures
- `CancellationException` is always rethrown, never caught
- `TimeoutCancellationException` is caught and wrapped (not rethrown)

---

## Compose conventions

- `modifier: Modifier = Modifier` — always present, used only at the root layout node
- Use `FreudText(value = ..., color = ..., typography = ...)` — parameter is `value`, not `text`
- Use `FreudDsToken<Color>`, `FreudDsToken<TextStyle>`, `FreudDsToken<Dp>` in component APIs — never raw values
- Booleans: `isEnabled`, `isVisible`, `shouldDisplayText` — never bare `enabled`
- Conditional modifiers: `modifier.then(if (condition) Modifier.X() else Modifier)`
- Use `heightIn(min = ...)` over `height(...)` for components containing text
- Previews are always `private`
- No code comments — code should be self-describing; KDoc for public APIs is allowed

---

## Build logic (convention plugins)

The `build-logic/` directory contains Gradle convention plugins:

| Plugin                          | Purpose                                              |
|---------------------------------|------------------------------------------------------|
| `base.androidApplication`       | Android application configuration                    |
| `base.desktopApplication`       | Desktop (JVM) application configuration              |
| `base.sharedLibrary`            | Shared KMP library configuration                     |
| `base.webApplication`           | WasmJs application configuration                     |
| `internal.config.detekt`        | Detekt + formatting + Compose lint plugins            |
| `internal.config.android`       | Android SDK configuration                            |
| `component.compose`             | Compose Multiplatform plugin setup                   |
| `component.composeNavigation`   | Navigation3 dependencies                             |
| `component.koin`                | Koin dependencies                                    |
| `component.mvi`                 | MVIKotlin dependencies                               |
| `component.serialization`       | kotlinx.serialization plugin + dependencies          |

Rules:
- New modules must apply convention plugins — never configure Kotlin/Android/Detekt manually
- Keep `build.gradle.kts` files minimal: apply plugins + declare dependencies

---

## Boundaries

### ✅ Always do
- run Android build + Detekt after every task
- use feature `api/di/internal` split for new features
- use `FreudTheme` and design-system tokens for all UI
- use `Outcome` for error handling
- use MVIKotlin Store + ViewModel pattern for screens
- wrap screens in `FreudFallback` and `FreudScreenByWidth`
- keep everything `internal` by default in feature modules
- use convention plugins from `build-logic/`

### ⚠️ Ask first
- adding new Gradle dependencies
- modifying `build-logic/` convention plugins
- modifying the `core:design-system` public API surface
- modifying `shared/App.kt` or navigation wiring
- changing `NavKey` definitions (affects navigation globally)

### 🚫 Never do
- hard-code colors, dimensions, or typography in feature modules
- put business logic in ViewModel (belongs in Store)
- use raw Material3 components without wrapping through the design system
- use Android-only APIs in `commonMain`
- leave code comments (no-comments policy — KDoc for public APIs only)
- commit failing Detekt or failing builds

---

## Adding a new feature — step by step

1. **`feature/<name>/api`** — create `@Serializable data object <Name>ScreenKey : NavKey` and `interface <Name>ScreenRouteHolderContract : ScreenRouteHolderContract`
2. **`feature/<name>/internal`** — implement:
   - `ui/theme/<Name>Theme.kt` — `internal object <Name>Theme : FreudTheme()` with semantic colors
   - `ui/screen/<Name>Store.kt` — Store interface (Intent/State/Label) + StoreFactory (Action/Message + bootstrapper + executor + reducer)
   - `ui/screen/<Name>ViewModel.kt` — thin ViewModel bridge
   - `ui/screen/<Name>Screen.kt` — screen composable using `koinViewModel`, `FreudFallback`, `FreudScreenByWidth`
   - `ui/route/<Name>ScreenRouteHolder.kt` — implements the route holder contract
3. **`feature/<name>/di`** — create `val <name>Module get() = module { ... }` with StoreFactory, ViewModel, RouteHolder
4. **`di/modules`** — add module to `featureModules` list
5. **`shared/App.kt`** — add `entry<ScreenKey> { routeHolder.content() }` to `entryProvider`
6. **`settings.gradle.kts`** — include `:feature:<name>:api`, `:feature:<name>:internal`, `:feature:<name>:di`
7. **Verify** — run `./gradlew app:headwayAndroid:assembleDebug` and `./gradlew detekt`
