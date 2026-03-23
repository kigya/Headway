---
icon: mobile
---

# Клиент: конвенции и рабочий процесс

Полный канон — файл [`client/AGENTS.md`](https://github.com/kigya/Headway/blob/trunk/client/AGENTS.md) в репозитории. Ниже — сжатая выжимка для ежедневной работы.

## Стек

Kotlin Multiplatform, **Compose Multiplatform** (Android, iOS, Desktop, Web), **MVIKotlin**, **Koin**, **Navigation3**, обработка ошибок через **`Outcome`** из `core:outcome`, UI через дизайн-систему **Freud** (`core:design-system`). Статический анализ: Detekt, ktlint, Compose lint.

## Команды проверки

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
cd client && ./gradlew detekt
```

После заметных правок в коде клиента эти шаги считаются обязательными перед сдачей задачи.

## Структура репозитория (логика)

* `app/headway*` — точки входа под платформы.
* `shared/` — сборка приложения: Koin, тема, `NavDisplay`.
* `core/design-system/` — токены и компоненты Freud (отдельный `AGENTS.md` внутри модуля).
* `core/outcome/` — типизированные успех/ошибка.
* `feature/<имя>/` — всегда три модуля: **`api`**, **`di`**, **`internal`**.

## Фича: api / di / internal

* **`api`** — только `NavKey` (например `@Serializable data object AuthScreenKey`) и контракт route holder. Без UI и бизнес-логики.
* **`di`** — Koin-модуль: `factoryOf(StoreFactory)`, `viewModelOf(ViewModel)`, `singleOf(RouteHolder) bind Contract`.
* **`internal`** — экраны, Store, ViewModel, темы, реализации route holder; по умолчанию всё **`internal`**.

Пакеты внутри `internal`: `ui/route`, `ui/screen`, `ui/theme`.

## MVIKotlin

* Интерфейс **`Store`** с `Intent`, `State`, `Label`.
* **`State`** помечайте `@Immutable`, поля с значениями по умолчанию.
* **`StoreFactory`** — `coroutineBootstrapper`, `coroutineExecutorFactory`, приватные `Action` / `Message`, **`Reducer`** с исчерпывающим `when` без `else`.
* **`ViewModel`** — тонкий мост: создаёт store, `store.accept(Intent)`, отдаёт `stateFlow(viewModelScope)`. **Бизнес-логики в ViewModel быть не должно.**

## Навигация

Только **`NavigatorContract`** и **`NavigationIntent`** (`NavigateTo`, `NavigateBack`, `ReplaceTopBy`). Прямые вызовы платформенной навигации или строковые роуты вне этого контракта не используются.

Новый экран: ключ и контракт в `api`, реализация holder, регистрация в DI, **`entry` в `shared/App.kt`**, включение модулей в `settings.gradle.kts`.

## Ошибки

Ожидаемые сбои моделируйте через **`Outcome`** и запечатанные типы ошибок, а не через общие исключения. `CancellationException` не глотаем; `TimeoutCancellationException` — по правилам из `client/AGENTS.md`.

## UI и Freud

* Тема фичи: `internal object XTheme : FreudTheme()`, цвета через расширения `FreudColorScheme` и **`FreudDynamicColor`**, палитра из **`super.color.*`**, не сырые `Color(0xFF…)`.
* Компоненты и токены: **`FreudText`**, **`FreudDsToken`**, компоненты дизайн-системы. Сырой Material3 в фичах без обёртки — нет.
* Экраны: обёртка **`FreudFallback`**, адаптивность **`FreudScreenByWidth`**, состояние через **`collectAsStateWithLifecycle()`**.
* У composable **`modifier: Modifier = Modifier`**; цепочку модификаторов вешайте на корневой узел раскладки.
* Параметр текста у `FreudText` — **`value`**, не `text`.

## Gradle

Новые модули подключают **convention plugins** из `client/build-logic`, руками не дублируют настройку Kotlin/Compose/Detekt. Список плагинов и назначение — таблица в `client/AGENTS.md`.

## Перед крупными изменениями согласуйте

* новые зависимости;
* правки `build-logic/`;
* публичный API `core:design-system`;
* глобальную навигацию и `shared/App.kt`.

## Новая фича (краткий чеклист)

1. `api`: `NavKey` + контракт route holder.
2. `internal`: тема, Store + Factory, ViewModel, Screen, RouteHolder.
3. `di`: модуль Koin.
4. Подключить модуль в `featureModules`, добавить `entry` в `App.kt`, `settings.gradle.kts`.
5. `assembleDebug` + `detekt`.
