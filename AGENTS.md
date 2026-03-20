# Cursor Rules for Headway Kotlin / Compose / Server Code

## Purpose

Use this file as the **single source of truth for Cursor** when generating or editing Kotlin code in this project.

The goal is simple:

- write code that is already compliant with the project's active **Detekt**, **ktlint formatting**, and **Compose** rules;
- prefer the **stricter shared interpretation** when client and server configs differ;
- avoid relying on auto-correction or later cleanup;
- generate code that is readable, maintainable, and unlikely to fail static analysis on the first pass.

This document is written for **Cursor prompt/rules usage**, not as a generic Detekt reference.

---

## Scope

Apply these instructions by default to:

- `*.kt` production code;
- shared KMP code;
- Android / Compose UI code;
- backend Kotlin code.

When a rule differs between client and server, use the stricter default unless a section explicitly says otherwise.

**Practical default thresholds to follow everywhere:**

- **Cyclomatic complexity:** keep functions at **13 or below**
- **Function parameter count:** keep public and private functions at **6 parameters or below**
- **Constructor parameter count:** keep constructors at **7 parameters or below**
- **Long method:** keep functions comfortably below **100 lines**
- **Nested block depth:** keep nesting at **5 or below**
- **Return count:** keep to **4 or fewer returns**
- **Throws count:** keep to **3 or fewer throws**
- **String literal duplication:** do not repeat the same string literal **more than twice** in production code
- **Line length:** hard limit **120**, but prefer:
    - **class signatures <= 80**
    - **function signatures <= 100**

---

## Non-negotiable Cursor behavior

Cursor must follow these rules in every Kotlin answer unless the user explicitly asks otherwise:

1. **Always include a package declaration** in `.kt` files.
2. **Never use wildcard imports**, except `java.util.*` when absolutely needed.
3. **Never leave `TODO`, `FIXME`, or `STOPSHIP` comments** in generated code.
4. **Never use `print` or `println`** in production code. Use a logger or proper output abstraction.
5. **Never use `lateinit`** unless the user explicitly requires it and there is no cleaner option.
6. **Never use `!!`** unless there is a proven, tightly scoped invariant and no safe alternative.
7. **Never call `System.exit`, `Runtime.exit`, `Runtime.halt`, `exitProcess`, `System.gc`, `Runtime.gc`, `System.runFinalization`, `printStackTrace`, `Thread.sleep` inside suspend/coroutine code, `TODO()`, or `NotImplementedError()`**.
8. **Never pass ViewModels or mutable state down the Compose tree** when a stateless API with data + callbacks is possible.
9. **Never reuse the incoming `modifier` on child nodes** in Compose.
10. **Always use 4 spaces**, no tabs, no trailing whitespace, and end files with a newline.
11. **Always use trailing commas** in multiline declarations and multiline call sites.
12. **Always name booleans with `is`, `has`, `are`, or `can`.**
13. **Always prefer explicit, narrow, deterministic code** over clever or overly compact code.

---

## Short self-checklist Cursor should run before finishing

Before returning Kotlin code, Cursor should verify:

- Does every file have a correct `package` declaration?
- Are imports explicit, ordered, and unused imports removed?
- Are there any `TODO`, `FIXME`, `STOPSHIP`, `println`, `lateinit`, `!!`, or placeholder implementations?
- Are multiline signatures wrapped correctly, with trailing commas?
- Are booleans named correctly?
- Are magic numbers extracted unless they are `-1`, `0`, `1`, `2`, or `0.5`?
- Are collection chains too long and better expressed with `asSequence()`?
- Are `when` branches exhaustive for enums / sealed classes / booleans without `else`?
- Are exceptions meaningful, specific, and created with a message or cause?
- For Compose:
    - is `modifier` present when needed, named `modifier`, defaulted to `Modifier`, and used only at the root?
    - is the composable stateless where possible?
    - are previews `private`?
    - is a `ViewModel` dependency explicit rather than hidden in the body?
    - is a Unit-returning composable named in `PascalCase`?

---

# Core authoring contract for Cursor

## 1. File, package, and import rules

### Always declare packages

Every Kotlin source file must start with a package declaration.

Bad:

```kotlin
class AuthRepository
```

Good:

```kotlin
package dev.kigya.headway.feature.auth.data

class AuthRepository
```

### Package naming

Use lowercase package segments, no underscores, no random abbreviations. Prefer meaningful hierarchical packages.

Good:

```kotlin
package dev.kigya.headway.feature.profile.presentation
```

Avoid:

```kotlin
package dev.kigya.headway.Feature_Profile.presentation_layer
```

### Imports

- Use explicit imports.
- Do not use wildcard imports except `java.util.*` if truly necessary.
- Remove unused imports.
- Keep imports ordered consistently.

Bad:

```kotlin
import dev.kigya.headway.feature.auth.*
import kotlin.collections.*
```

Good:

```kotlin
import dev.kigya.headway.feature.auth.api.AuthRepository
import kotlin.collections.List
```

### Package / import spacing

Keep exactly one blank line between package and imports, and one blank line between imports and declarations.

Good:

```kotlin
package dev.kigya.headway.core.common

import kotlin.math.abs

class Example
```

---

## 2. Naming rules

### Classes, objects, enums, and top-level types

Use `PascalCase`.

Good:

```kotlin
class AuthRepository
object FreudTheme
enum class ScreenState
```

### Functions

Use `camelCase`.

Good:

```kotlin
fun loadUserProfile()
fun mapToDomain()
```

Avoid names shorter than 2 characters unless the context is extremely local and obvious.

Bad:

```kotlin
fun x()
```

### Constructor parameters, function parameters, local variables, and properties

Use `camelCase`.

Good:

```kotlin
class UserRepository(
    private val apiClient: ApiClient,
)

fun loadProfile(userId: String)

val authState = ...
```

### Boolean naming

Boolean names must start with `is`, `has`, `are`, or `can`.

Bad:

```kotlin
val enabled: Boolean
val progressBar: Boolean
```

Good:

```kotlin
val isEnabled: Boolean
val hasProgressBar: Boolean
val canRetry: Boolean
val areAllItemsLoaded: Boolean
```

### `is*` prefix only for booleans

Do not use an `is` prefix for non-boolean properties.

Bad:

```kotlin
val isCount: Int = 3
```

Good:

```kotlin
val count: Int = 3
```

### Member name must not equal class name

Do not create members whose names mirror the containing class.

Bad:

```kotlin
class ProfileState {
    fun profileState() {}
}
```

Good:

```kotlin
class ProfileState {
    fun reset() {}
}
```

### Compose naming specifics

- **Unit-returning `@Composable` functions** use **PascalCase**
- **Value-returning `@Composable` functions** use **camelCase**
- `CompositionLocal`s use `Local...`
- preview annotations use `Previews...`
- event parameters use `on...` in **present tense**

Good:

```kotlin
@Composable
fun ProfileScreen(...)

@Composable
fun rememberProfileState(...): ProfileState

val LocalSpacing = staticCompositionLocalOf { Spacing() }

@Composable
fun ProfileCard(
    onClick: () -> Unit,
)
```

Avoid:

```kotlin
@Composable
fun profileScreen(...)

@Composable
fun ProfileState(): ProfileState

val SpacingLocal = ...

@Composable
fun ProfileCard(
    onClicked: () -> Unit,
)
```

### Top-level property naming

Use:

- `UPPER_SNAKE_CASE` for constants,
- `camelCase` for normal top-level vals,
- `PascalCase` only when Compose guidelines or design tokens clearly justify it.

Good:

```kotlin
const val MAX_RETRY_COUNT = 3
private val defaultTimeoutMillis = 5_000L
private val CardHorizontalPadding = 16.dp
```

---

## 3. Formatting contract

Formatting is heavily enforced in this project. Cursor should write code in a way that matches ktlint-backed Detekt rules **before** auto-correct runs.

### Base formatting rules

- 4-space indentation
- no tabs
- no semicolons
- no trailing spaces
- final newline required
- no consecutive blank lines
- no blank line before `}`
- no blank lines inside parameter or argument lists
- no blank lines inside chained call sequences

### Annotation formatting

Put annotations on their own line where appropriate.

Good:

```kotlin
@Composable
private fun ProfilePreview() {
    ...
}
```

### Function and class signatures

Use a multiline signature early, not late.

**Project-safe default:** if a function has **2 or more parameters**, strongly prefer multiline formatting.

Good:

```kotlin
private fun buildRequest(
    userId: String,
    locale: Locale,
): UserRequest = UserRequest(
    userId = userId,
    locale = locale,
)
```

Also keep:

- class signatures comfortably under **80 chars**
- function signatures under **100 chars**
- absolute line length under **120 chars**

### Trailing commas

Always use trailing commas in multiline declarations and multiline call sites.

Good:

```kotlin
data class UserUiState(
    val id: String,
    val title: String,
)

val state = UserUiState(
    id = "1",
    title = "Profile",
)
```

### Chained calls

If a chain wraps, wrap the whole chain consistently. Do not mix styles.

Bad:

```kotlin
repository.load()
    .map { it.toUi() }.filter { it.isVisible }
```

Good:

```kotlin
repository
    .load()
    .map { it.toUi() }
    .filter { it.isVisible }
```

### Braces

#### `if`
- single-line: braces only when necessary
- multi-line: always use braces

Bad:

```kotlin
if (isReady)
    execute()
```

Good:

```kotlin
if (isReady) {
    execute()
}
```

#### `when`
Use braces only when necessary for multi-statement branches, but keep style consistent and readable.

Good:

```kotlin
return when (result) {
    is Result.Success -> result.value
    is Result.Failure -> {
        logger.error(result.error)
        null
    }
}
```

### Spacing

Respect standard Kotlin spacing around:

- annotations
- commas
- colons
- operators
- dots
- parentheses
- braces
- angle brackets
- double colons
- range operators

### Comments

- keep comment spacing correct;
- prefer `//` over single-line block comments;
- keep KDoc wrapping clean when KDoc is present.

---

## 4. Complexity and structural rules

### Keep conditions simple

Avoid conditions with more than **5 logical parts**. Extract subconditions into named values or helper functions.

Bad:

```kotlin
if (isEnabled && isAuthorized && hasToken && hasNetwork && !isExpired && !isBlocked) {
    ...
}
```

Good:

```kotlin
val canExecute =
    isEnabled &&
        isAuthorized &&
        hasToken &&
        hasNetwork &&
        !isExpired &&
        !isBlocked

if (canExecute) {
    ...
}
```

Better:

```kotlin
val canExecute = canExecuteRequest()

if (canExecute) {
    ...
}
```

### Keep cyclomatic complexity low

Do not let one function accumulate too many:

- `if`
- `when`
- loops
- `catch`
- `&&` / `||`
- `forEach`
- scope functions used as control flow

Best practice:

- split decision branches into named helpers;
- replace nested control flow with guard clauses;
- move mapping and validation to dedicated functions.

### Avoid deep nesting

Keep nesting depth at **5 or below**, ideally much lower.

Bad:

```kotlin
if (a) {
    if (b) {
        if (c) {
            for (item in items) {
                if (item.isValid) {
                    ...
                }
            }
        }
    }
}
```

Good:

```kotlin
if (!a || !b || !c) return

for (item in items) {
    if (!item.isValid) continue
    ...
}
```

### Avoid long parameter lists

Keep functions under **6 parameters**. Prefer:

- a parameter object,
- a config object,
- a domain model,
- grouping related values into a value type.

Bad:

```kotlin
fun createUser(
    id: String,
    name: String,
    email: String,
    age: Int,
    isAdmin: Boolean,
    locale: Locale,
    createdAt: Instant,
)
```

Good:

```kotlin
data class CreateUserParams(
    val id: String,
    val name: String,
    val email: String,
    val age: Int,
    val isAdmin: Boolean,
    val locale: Locale,
    val createdAt: Instant,
)

fun createUser(params: CreateUserParams)
```

### Use named arguments for 4+ arguments

If a call has **4 or more arguments**, use named arguments.

Bad:

```kotlin
loadUser("42", true, false, Locale.US)
```

Good:

```kotlin
loadUser(
    userId = "42",
    forceRefresh = true,
    includePosts = false,
    locale = Locale.US,
)
```

### Avoid duplicated string literals

If the same string literal appears more than twice, extract it.

Bad:

```kotlin
logger.info("network")
cache.put("network", value)
metrics.increment("network")
```

Good:

```kotlin
private const val SOURCE_NETWORK = "network"

logger.info(SOURCE_NETWORK)
cache.put(SOURCE_NETWORK, value)
metrics.increment(SOURCE_NETWORK)
```

### Keep files and classes focused

- avoid classes over ~600 lines;
- avoid too many functions in one file/class/object/interface/enum;
- split by responsibility rather than by arbitrary size.

### Prefer `?.run { ... }` over redundant safe-call tails

Bad:

```kotlin
val path = fileSystem
    ?.root
    ?.absolutePath
    ?.lowercase()
```

Good:

```kotlin
val path = fileSystem?.run {
    root.absolutePath.lowercase()
}
```

---

## 5. Null-safety and bug-prevention rules

### Never use referential equality on Strings

Use `==` / `!=`, not `===` / `!==`, for strings and similar value objects.

Bad:

```kotlin
if (value === "ok") { ... }
```

Good:

```kotlin
if (value == "ok") { ... }
```

### Avoid unsafe casts and unsafe nullable calls

Prefer safe casts and proper null handling.

Bad:

```kotlin
val name = value as String
println(name!!.length)
```

Good:

```kotlin
val name = value as? String ?: return
println(name.length)
```

### Avoid `!!`

If something can be null, handle it explicitly.

Bad:

```kotlin
val tokenLength = token!!.length
```

Good:

```kotlin
val tokenLength = token?.length ?: 0
```

### Avoid unnecessary safe calls and unnecessary not-null checks

Do not write nullable syntax around non-null values.

Bad:

```kotlin
val name: String = "A"
val length = name?.length
```

Good:

```kotlin
val name: String = "A"
val length = name.length
```

### Do not null-check mutable properties directly

Snapshot mutable nullable properties into a local val before checking.

Bad:

```kotlin
if (state.value != null) {
    use(state.value!!)
}
```

Good:

```kotlin
val currentValue = state.value ?: return
use(currentValue)
```

### Do not downcast immutable collections

If you need a mutable collection, create one explicitly.

Bad:

```kotlin
(val list as MutableList).add(item)
```

Good:

```kotlin
val mutableList = list.toMutableList()
mutableList.add(item)
```

### Avoid double mutability

Do not declare mutable collections as `var` unless you truly replace the collection object.

Bad:

```kotlin
var items = mutableListOf<String>()
```

Good:

```kotlin
val items = mutableListOf<String>()
```

Or:

```kotlin
var items = listOf<String>()
```

### Exhaustive `when` for sealed / enum / boolean

Do not use `else` when the subject is exhaustive.

Bad:

```kotlin
return when (state) {
    State.Loading -> "loading"
    State.Success -> "success"
    else -> "error"
}
```

Good:

```kotlin
return when (state) {
    State.Loading -> "loading"
    State.Success -> "success"
    State.Error -> "error"
}
```

### Use explicit return types when platform types may leak

Do not expose platform types accidentally.

Bad:

```kotlin
fun property() = System.getProperty("key")
```

Good:

```kotlin
fun property(): String? = System.getProperty("key")
```

### Use return values that must not be ignored

Always consume results from:

- functions annotated with `@CheckResult` / `@CheckReturnValue`;
- `Sequence`;
- `Flow`;
- Java streams.

Bad:

```kotlin
repository.refresh()
flow.map { it.toUi() }
```

Good:

```kotlin
val refreshResult = repository.refresh()
val uiFlow = flow.map { it.toUi() }
```

### Always specify `Locale` for machine-readable formatting

Bad:

```kotlin
val text = String.format("id=%d", value)
```

Good:

```kotlin
val text = String.format(Locale.US, "id=%d", value)
```

### Never call `map["key"]!!`

Use safer accessors.

Bad:

```kotlin
val value = map["key"]!!
```

Good:

```kotlin
val value = map.getValue("key")
```

Or:

```kotlin
val value = map.getOrDefault("key", defaultValue)
```

### Iterator correctness

If Cursor generates a custom `Iterator`:

- `hasNext()` must not call `next()`
- `next()` must throw `NoSuchElementException` when exhausted

### Avoid invalid ranges

Do not generate empty ranges like `2 until 2` or `10..9` unless that is truly intentional and documented.

### Remove unreachable code

Never leave statements after:

- `return`
- `throw`
- `break`
- `continue`

### Do not write useless postfix expressions

Bad:

```kotlin
i = i++
```

Good:

```kotlin
i++
```

### `equals` / `hashCode`

If you override `equals`, also override `hashCode`. `equals` must:

- accept `Any?`
- not always return constant `true` or `false`

Bad:

```kotlin
override fun equals(other: Any?): Boolean = true
```

Good:

```kotlin
override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is UserId) return false

    return value == other.value
}

override fun hashCode(): Int = value.hashCode()
```

---

## 6. Exception rules

### Do not throw generic exceptions

Use a specific exception type.

Bad:

```kotlin
throw Exception("failed")
```

Good:

```kotlin
throw IllegalStateException("User session is missing")
```

### Always provide message or cause

Bad:

```kotlin
throw IllegalArgumentException()
```

Good:

```kotlin
throw IllegalArgumentException("userId must not be blank")
```

### Do not throw in `toString`, `hashCode`, `equals`, `finalize`

These methods must be safe and predictable.

### Do not check exception type inside a catch block

Use multiple catch blocks instead of `is` checks inside `catch`.

Bad:

```kotlin
catch (e: IOException) {
    if (e is FileNotFoundException) { ... }
}
```

Good:

```kotlin
catch (e: FileNotFoundException) {
    ...
} catch (e: IOException) {
    ...
}
```

### Do not leave placeholder implementations

Never generate:

- `TODO()`
- `NotImplementedError()`

### Throwable types must be classes, not objects

Bad:

```kotlin
object AuthException : RuntimeException()
```

Good:

```kotlin
class AuthException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
```

### Do not use `printStackTrace`

Use a logger.

Bad:

```kotlin
catch (e: IOException) {
    e.printStackTrace()
}
```

Good:

```kotlin
catch (e: IOException) {
    logger.error(e) { "Failed to load user profile" }
}
```

### Do not catch and immediately rethrow unchanged

If you catch, either:

- add context,
- map to a more meaningful type,
- or do real work before rethrowing.

Bad:

```kotlin
catch (e: IOException) {
    throw e
}
```

Good:

```kotlin
catch (e: IOException) {
    throw ProfileLoadException("Failed to load profile", e)
}
```

### Do not return from `finally`

Do not hide exceptions by returning from `finally`.

### Do not throw from `finally`

Cleanup must not replace the original failure.

### Do not wrap an exception in the same exception type

Bad:

```kotlin
catch (e: IllegalStateException) {
    throw IllegalStateException(e)
}
```

Good:

```kotlin
catch (e: IllegalStateException) {
    throw ProfileLoadException("Profile state is invalid", e)
}
```

---

## 7. Coroutine rules

### Do not mark a function `suspend` unless it actually needs suspension

Bad:

```kotlin
suspend fun formatName(name: String): String = name.trim()
```

Good:

```kotlin
fun formatName(name: String): String = name.trim()
```

### Never use `Thread.sleep` in suspend or coroutine code

Use `delay`.

Bad:

```kotlin
suspend fun retry() {
    Thread.sleep(1_000)
}
```

Good:

```kotlin
suspend fun retry() {
    delay(1_000)
}
```

### Do not mix `suspend` with `CoroutineScope` receiver

Bad:

```kotlin
suspend fun CoroutineScope.load() { ... }
```

Good:

```kotlin
fun CoroutineScope.load() { ... }
```

Or:

```kotlin
suspend fun load() = coroutineScope {
    ...
}
```

### Functions returning `Flow` must not be `suspend`

Bad:

```kotlin
suspend fun observe(): Flow<State> = flow { ... }
```

Good:

```kotlin
fun observe(): Flow<State> = flow { ... }
```

---

## 8. Performance rules

### Use primitive arrays instead of boxed arrays

Bad:

```kotlin
fun sum(values: Array<Int>)
```

Good:

```kotlin
fun sum(values: IntArray)
```

### Use `Sequence` for long collection pipelines

If there are **3 or more chained collection operations**, prefer `asSequence()` unless materializing eagerly is important.

Bad:

```kotlin
items.map { it.a }.filter { it.isValid }.map { it.toUi() }
```

Good:

```kotlin
items
    .asSequence()
    .map { it.a }
    .filter { it.isValid }
    .map { it.toUi() }
    .toList()
```

### Do not use `forEach` on ranges

Bad:

```kotlin
(0 until count).forEach { index ->
    process(index)
}
```

Good:

```kotlin
for (index in 0 until count) {
    process(index)
}
```

### Avoid spread operator on existing arrays

Bad:

```kotlin
val result = build(*values)
```

Good:

```kotlin
val result = build(
    values[0],
    values[1],
)
```

Or construct inline when appropriate.

### Remove redundant binary parts

Bad:

```kotlin
if (isVisible || isHidden || isVisible) { ... }
```

Good:

```kotlin
if (isVisible || isHidden) { ... }
```

### Avoid temporary boxed instances just for conversion

Bad:

```kotlin
val text = Integer(1).toString()
```

Good:

```kotlin
val text = Integer.toString(1)
```

---

## 9. Style and API rules

### Prefer `apply` when an `also` block only mutates the receiver

Bad:

```kotlin
User().also {
    it.init()
    it.validate()
}
```

Good:

```kotlin
User().apply {
    init()
    validate()
}
```

### Use braces consistently for `if` and `when`

- multi-line `if` branches: always brace
- `when` branches: brace when necessary for multiple statements

### Make nullable types non-nullable when possible

If code immediately checks for null and proceeds as non-null, change the API to non-null if possible.

### Keep class ordering consistent

Order class contents as:

1. properties and init blocks
2. secondary constructors
3. methods
4. companion object

### Data classes should stay data-oriented

- pure data holder -> `data class`
- data classes should be mostly immutable
- data classes should not contain random business logic
- only lightweight conversion helpers like `toX()` / `mapToX()` are acceptable

Bad:

```kotlin
data class UserUiModel(
    var id: String,
) {
    fun fetchFromNetwork() { ... }
}
```

Good:

```kotlin
data class UserUiModel(
    val id: String,
)

fun UserUiModel.toDomain(): User = User(id = id)
```

### Keep destructuring small

Do not destructure more than 5 values. Prefer using the object directly or a named value object.

### Avoid double negatives

Bad:

```kotlin
items.takeUnless { !it.isVisible }
```

Good:

```kotlin
items.takeIf { it.isVisible }
```

### Use `== null`, not `equals(null)`

Bad:

```kotlin
value.equals(null)
```

Good:

```kotlin
value == null
```

### Prefer expression bodies for single-return functions

Bad:

```kotlin
fun id(): String {
    return "42"
}
```

Good:

```kotlin
fun id(): String = "42"
```

### Use Kotlin annotations, not Java annotation types

Prefer Kotlin / androidx variants over forbidden Java annotation forms.

Bad:

```kotlin
@java.lang.Deprecated
```

Good:

```kotlin
@Deprecated("Use new API")
```

### Never leave forbidden comments

Bad:

```kotlin
// TODO remove later
```

Good:

```kotlin
// Explain why this branch exists.
```

### Never use `print` / `println`

Bad:

```kotlin
println("loaded")
```

Good:

```kotlin
logger.info { "Loaded user profile" }
```

### Do not use `Void`

Use `Unit`.

Bad:

```kotlin
val callback: () -> Void
```

Good:

```kotlin
val callback: () -> Unit
```

### Replace constant-returning functions with constants

Bad:

```kotlin
fun defaultTimeout() = 5_000L
```

Good:

```kotlin
private const val DEFAULT_TIMEOUT_MILLIS = 5_000L
```

### Avoid loops with too many jumps

Refactor loops with more than one `break` / `continue`.

### Extract magic numbers

Allowed inline numbers are effectively only:

- `-1`
- `0`
- `1`
- `2`
- `0.5`

Everything else should usually be a named constant unless the context is obvious and exempt.

Bad:

```kotlin
if (username.length > 42) { ... }
```

Good:

```kotlin
private const val MAX_USERNAME_LENGTH = 42

if (username.length > MAX_USERNAME_LENGTH) { ... }
```

### Use `const val` when possible

Bad:

```kotlin
private val defaultHost = "api.example.com"
```

Good:

```kotlin
private const val DEFAULT_HOST = "api.example.com"
```

### Order modifiers correctly

Use normal Kotlin modifier order.

Good:

```kotlin
internal data class UserId(
    val value: String,
)
```

### Multiline lambdas must have meaningful parameter names

For multiline lambdas, do not rely on implicit or explicit `it` when readability suffers.

Bad:

```kotlin
items.forEach {
    logger.info { it.id }
    process(it)
}
```

Better:

```kotlin
items.forEach { item ->
    logger.info { item.id }
    process(item)
}
```

### Prefer lambda over object literal for SAM types

Bad:

```kotlin
executor.execute(object : Runnable {
    override fun run() {
        refresh()
    }
})
```

Good:

```kotlin
executor.execute {
    refresh()
}
```

### Avoid redundant `abstract`, `Unit`, visibility modifiers, backticks, inheritance, `inner`, parentheses, `let`, `apply`

Cursor should remove ceremony that adds no meaning.

Examples:

Bad:

```kotlin
abstract interface UserStore
fun refresh(): Unit { ... }
public class UserRepository
class `ProfileScreen`
class A : Any()
```

Good:

```kotlin
interface UserStore
fun refresh() { ... }
class UserRepository
class ProfileScreen
class A
```

### Use indexed access for collections

Bad:

```kotlin
map.put("id", value)
val id = map.get("id")
```

Good:

```kotlin
map["id"] = value
val id = map["id"]
```

### Prefer `to` over manual `Pair(...)`

Bad:

```kotlin
val pair = Pair("a", 1)
```

Good:

```kotlin
val pair = "a" to 1
```

### Prefer `0 until n` / `0..<n` style correctly

Use `until` instead of `.. lastIndex` style when the upper bound is excluded.

Bad:

```kotlin
for (index in 0..count - 1) { ... }
```

Good:

```kotlin
for (index in 0 until count) { ... }
```

### Serializable classes must define `serialVersionUID`

```kotlin
class Snapshot : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
```

### Use raw strings when escaping becomes noisy

Bad:

```kotlin
val json = "{\n  \"id\": \"1\"\n}"
```

Good:

```kotlin
val json = """
    {
      "id": "1"
    }
""".trimIndent()
```

### Use underscores in long numeric literals

Bad:

```kotlin
private const val CACHE_TTL_MILLIS = 600000
```

Good:

```kotlin
private const val CACHE_TTL_MILLIS = 600_000
```

### Remove dead code immediately

Do not generate unused:

- imports
- private classes
- private members
- private properties

If something is intentionally unused, name it `_`, `ignored`, `expected`, or `serialVersionUID` where applicable.

### Prefer dedicated helpers

Use:
- `check`, `checkNotNull`, `error` for state/invariants
- `require`, `requireNotNull` for caller preconditions
- `orEmpty`, `isNullOrEmpty`, `ifEmpty`, `ifBlank` where they simplify code
- `any` / `none` instead of `find() != null` / `find() == null`
- `sumOf` instead of `flatMap().size`
- `safe cast` (`as?`) when a cast may legitimately fail

Examples:

Bad:

```kotlin
if (userId == null) {
    throw IllegalArgumentException("userId is null")
}
```

Good:

```kotlin
requireNotNull(userId) { "userId must not be null" }
```

Bad:

```kotlin
val title = maybeTitle ?: ""
```

Good:

```kotlin
val title = maybeTitle.orEmpty()
```

Bad:

```kotlin
if (items == null || items.isEmpty()) return
```

Good:

```kotlin
if (items.isNullOrEmpty()) return
```

Bad:

```kotlin
list.find { it.id == id } != null
```

Good:

```kotlin
list.any { it.id == id }
```

---

## 10. Compose-specific rules

These rules apply to all `@Composable` code.

### Top-level composables

Composable functions should usually be top-level, not hidden inside objects, unless there is a very strong reason.

### Composable function naming

- Unit-returning composables: `PascalCase`
- value-returning composables: `camelCase`

Good:

```kotlin
@Composable
fun ProfileScreen(...)

@Composable
fun rememberProfileScrollState(...): LazyListState
```

### Parameter order for composables

Use this order:

1. required parameters
2. required event lambdas
3. `modifier: Modifier = Modifier` as the **first optional parameter**
4. other optional parameters
5. trailing `content` lambda, if present

Good:

```kotlin
@Composable
fun ProfileCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    ...
}
```

### Event parameter naming

Use `onXxx` in present tense.

Good:

```kotlin
onClick
onDismiss
onValueChange
onRetry
```

Avoid:

```kotlin
clicked
onClicked
onDismissed
```

### Do not expose mutable parameters

Do not pass `MutableList`, `ArrayList`, mutable maps, mutable sets, or other inherently mutable objects to composables when a stable immutable value + callback API is possible.

Bad:

```kotlin
@Composable
fun TagsEditor(
    tags: MutableList<String>,
)
```

Good:

```kotlin
@Composable
fun TagsEditor(
    tags: List<String>,
    onTagsChange: (List<String>) -> Unit,
)
```

### Do not pass `MutableState<T>` as a composable parameter

Bad:

```kotlin
@Composable
fun NameField(
    text: MutableState<String>,
)
```

Good:

```kotlin
@Composable
fun NameField(
    text: String,
    onTextChange: (String) -> Unit,
)
```

### Do not forward ViewModels through the tree

Do not pass a `ViewModel` into leaf or mid-level composables when you can pass:

- plain UI state
- callbacks
- a derived state holder interface

Bad:

```kotlin
@Composable
fun ProfileContent(
    viewModel: ProfileViewModel,
)
```

Good:

```kotlin
@Composable
fun ProfileContent(
    state: ProfileUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
)
```

### Make ViewModel dependency explicit if needed

If a screen-level composable needs a ViewModel, expose it explicitly as a default parameter rather than resolving it invisibly deep in the body.

Good:

```kotlin
@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = viewModel(),
) {
    ...
}
```

### Remember state created inside composables

Bad:

```kotlin
@Composable
fun Counter() {
    val count = mutableStateOf(0)
    ...
}
```

Good:

```kotlin
@Composable
fun Counter() {
    val count = remember { mutableStateOf(0) }
    ...
}
```

### Remember movable content

If using `movableContentOf(...)`, wrap it in `remember`.

### Effect keys must be chosen carefully

For `LaunchedEffect`, `DisposableEffect`, `produceState`, and similar APIs:

- include real restart keys;
- avoid accidental stale captures;
- use `rememberUpdatedState` when you need the latest lambda/value without restarting the effect.

### Composables should either emit UI or return a value, not both

Bad:

```kotlin
@Composable
fun ProfileTitle(): String {
    Text("Profile")
    return "Profile"
}
```

Good:

```kotlin
@Composable
fun ProfileTitle() {
    Text("Profile")
}
```

Or:

```kotlin
@Composable
fun profileTitleText(): String = stringResource(...)
```

### Avoid multiple independent emitters in one composable

A composable should usually emit one cohesive UI subtree. If multiple sibling nodes are emitted, that should be intentional and usually tied to a scope receiver such as `ColumnScope`.

### `modifier` rules

#### Expose `modifier` when the composable represents a UI element

If the caller should influence outer layout, include:

```kotlin
modifier: Modifier = Modifier
```

#### Name it `modifier`

If a modifier applies to the root, call it exactly `modifier`.

If it applies to a sub-part, use a specific name like `iconModifier`.

#### Put it first among optional params

Good:

```kotlin
@Composable
fun Avatar(
    imageUrl: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
)
```

#### Use it at the root

Apply the incoming modifier only to the top-most layout node of the component.

Bad:

```kotlin
@Composable
fun UserRow(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = "Name",
            modifier = modifier,
        )
    }
}
```

Good:

```kotlin
@Composable
fun UserRow(
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(
            text = "Name",
            modifier = Modifier,
        )
    }
}
```

#### Do not reuse the incoming modifier on descendants

Child nodes must build their own modifiers from `Modifier`, not reuse the parent modifier instance.

### Avoid `Modifier.composed`

Prefer modern modifier APIs and explicit modifier building. Do not generate `Modifier.composed` unless the user explicitly asks for it and there is no better API.

### Condition lifting

If a whole layout subtree is conditional, lift the `if` above the container when that improves structure.

Bad:

```kotlin
Column {
    if (isVisible) {
        Text("A")
        Text("B")
    }
}
```

Good:

```kotlin
if (isVisible) {
    Column {
        Text("A")
        Text("B")
    }
}
```

### Use `heightIn()` instead of hard `height()` around text-heavy containers

If a layout contains text that may wrap, avoid clipping by preferring `heightIn()` over fixed `height()` unless a fixed height is intentional.

### Preview rules

- preview composables should be `private`
- preview annotations should follow naming conventions
- preview-only functions should not leak into production API surface

Good:

```kotlin
@Preview
@Composable
private fun ProfileCardPreview() {
    ProfileCard(
        title = "Profile",
        onClick = {},
    )
}
```

### CompositionLocal rules

- name them `LocalXxx`
- keep them rare
- allowlist only truly justified CompositionLocals
- prefer explicit parameters over hidden ambient dependencies

### Defaults object visibility

If a component has `Defaults`, keep its visibility aligned with the composable visibility.

### Unstable collections in Compose

Do not pass unstable mutable collections into composables. Prefer immutable collections or stable wrappers.

---

## 11. Client vs server differences

The configs are almost identical. To keep Cursor safe across the whole repo, use the stricter shared defaults.

### Use these stricter shared defaults everywhere

- cyclomatic complexity: **13**
- function parameter count: **6**
- string duplication threshold: **2**
- use named arguments from **4** args upward
- prefer `if` over trivial binary `when`
- package naming and package declarations always enforced
- Compose rules apply to all UI code

### Known non-critical differences

- Server disables one `UseIfInsteadOfWhen` rule that is enabled on client.
- Server uses slightly looser complexity and string duplication thresholds.
- Those differences do not justify writing weaker code. Stick to the stricter client-style baseline unless the user explicitly asks for a server-specific exception.

---

## 12. Recommended way to split Cursor rules

This repository is large enough that one giant Cursor rule file can become noisy. The best long-term setup is to split rules by scope.

Suggested structure:

```text
.cursor/rules/
  00-global-kotlin.md
  10-formatting.md
  20-compose.md
  30-server.md
  40-testing.md
```

### Best practices for Cursor rule authoring

1. **Keep one responsibility per rule file.**  
   Do not mix Compose API rules, backend rules, and formatting in the same small rule file unless it is an intentionally global file.

2. **Use the strictest stable defaults.**  
   If two modules differ slightly, encode the stricter rule globally and add a narrow exception rule only where needed.

3. **Prefer explicit instructions over vague language.**  
   Good: “Use `modifier: Modifier = Modifier` as the first optional parameter.”  
   Bad: “Write Compose code cleanly.”

4. **Include bad/good examples for every tricky convention.**  
   This is especially important for:
    - Compose parameter order
    - modifier reuse
    - state hoisting
    - naming
    - exception handling
    - multiline formatting

5. **Encode numeric thresholds directly.**  
   Examples:
    - line length 120
    - function params <= 6
    - return count <= 4

6. **Put universal constraints in one always-on file.**  
   Examples:
    - no wildcard imports
    - no TODO/FIXME/STOPSHIP
    - no println
    - no `!!`
    - no `lateinit`
    - package declaration required

7. **Put framework-specific constraints behind file globs.**  
   Examples:
    - Compose rules for `**/*Screen.kt`, `**/ui/**`, `**/*.compose.kt`
    - backend rules for `server/**`

8. **Avoid overlapping or contradictory rule wording.**  
   For example, do not tell Cursor both “always use let for null checks” and “avoid let.”  
   Instead write the true combined rule:
    - use `let` when it meaningfully simplifies null-flow;
    - do not use trivial `let` on non-null values.

9. **Write rules as generation constraints, not as review comments.**  
   Good: “Do not generate placeholder TODO implementations.”  
   Bad: “Try not to leave TODOs if possible.”

10. **End each rule file with a self-review checklist.**  
    Cursor follows checklists better than prose-only files.

---

## 13. Example minimal Cursor rule file frontmatter

If you want to convert this document into actual Cursor rule files, a good pattern is:

```md
---
description: Headway global Kotlin + Detekt contract
globs:
  - "**/*.kt"
alwaysApply: false
---

# Headway Kotlin contract

- Always include package declaration.
- Never use wildcard imports except java.util.*.
- Never use TODO/FIXME/STOPSHIP.
- Never use println/print.
- Never use lateinit unless explicitly requested.
- Never use !! when a safe alternative exists.
- Keep line length <= 120.
- Use trailing commas in multiline declarations and calls.
- Use camelCase for functions and variables, PascalCase for classes.
- Boolean names must start with is/has/are/can.
- Extract magic numbers except -1, 0, 1, 2, 0.5.
- Prefer require/check helpers over manual throws.
```

And a separate Compose rule file:

```md
---
description: Headway Compose contract
globs:
  - "**/ui/**/*.kt"
  - "**/*Screen.kt"
  - "**/*Content.kt"
alwaysApply: false
---

# Headway Compose contract

- Unit-returning composables use PascalCase.
- Value-returning composables use camelCase.
- Prefer stateless composables with state hoisting.
- Do not pass ViewModels down the tree.
- Do not pass MutableState<T> as a parameter.
- Do not pass mutable collections as parameters.
- Use modifier: Modifier = Modifier as the first optional parameter.
- Apply modifier only to the root node.
- Do not reuse incoming modifier on child nodes.
- Preview composables must be private.
```

---

## 14. Compact active rule map

This appendix lists the **active rules** from your configs in compact form. Use it as a direct mapping from rule name to generation behavior.

### Comments
- **CommentOverPrivateFunction:** disabled
- **CommentOverPrivateProperty:** disabled
- **DeprecatedBlockTag:** disabled
- **EndOfSentenceFormat:** disabled
- **KDocReferencesNonPublicProperty:** disabled
- **OutdatedDocumentation:** disabled
- **UndocumentedPublicClass:** disabled
- **UndocumentedPublicFunction:** disabled
- **UndocumentedPublicProperty:** disabled

### Complexity
- **ComplexCondition:** keep boolean expressions small; extract helpers after 5 conditions
- **CyclomaticComplexMethod:** keep control-flow complexity <= 13
- **LargeClass:** split large classes before they become broad god objects
- **LongMethod:** keep methods below 100 lines
- **LongParameterList:** keep functions <= 6 params, constructors <= 7
- **NamedArguments:** use named args for calls with 4+ arguments
- **NestedBlockDepth:** keep nesting <= 5
- **ReplaceSafeCallChainWithRun:** prefer `?.run {}` over trailing redundant safe calls
- **StringLiteralDuplication:** extract repeated string literals
- **TooManyFunctions:** split files/classes/objects/interfaces with too many functions

### Coroutines
- **RedundantSuspendModifier:** do not mark non-suspending functions `suspend`
- **SleepInsteadOfDelay:** use `delay`, never `Thread.sleep` in coroutines
- **SuspendFunWithCoroutineScopeReceiver:** avoid `suspend fun CoroutineScope.foo()`
- **SuspendFunWithFlowReturnType:** functions returning `Flow` must not be `suspend`

### Empty blocks
- **EmptyCatchBlock:** never swallow exceptions silently
- **EmptyClassBlock:** do not generate empty classes
- **EmptyDefaultConstructor:** do not generate empty primary/default constructors
- **EmptyDoWhileBlock:** do not generate empty loops
- **EmptyElseBlock:** do not generate empty `else`
- **EmptyFinallyBlock:** do not generate empty `finally`
- **EmptyForBlock:** do not generate empty loops
- **EmptyFunctionBlock:** do not generate empty functions
- **EmptyIfBlock:** do not generate empty `if`
- **EmptyInitBlock:** do not generate empty `init`
- **EmptyKtFile:** do not create empty files
- **EmptySecondaryConstructor:** do not generate empty secondary constructors
- **EmptyTryBlock:** do not generate empty `try`
- **EmptyWhenBlock:** do not generate empty `when`
- **EmptyWhileBlock:** do not generate empty `while`

### Exceptions
- **ExceptionRaisedInUnexpectedLocation:** do not throw from `equals`, `hashCode`, `toString`, `finalize`
- **InstanceOfCheckForException:** use multiple catch blocks, not `is` checks inside catch
- **NotImplementedDeclaration:** do not generate `TODO()` or `NotImplementedError()`
- **ObjectExtendsThrowable:** exceptions must be classes, not `object`
- **PrintStackTrace:** use logger, never `printStackTrace`
- **RethrowCaughtException:** do not catch only to rethrow unchanged
- **ReturnFromFinally:** never return from `finally`
- **ThrowingExceptionFromFinally:** never throw from `finally`
- **ThrowingExceptionsWithoutMessageOrCause:** always provide exception message or cause
- **ThrowingNewInstanceOfSameException:** do not wrap an exception in the same type
- **TooGenericExceptionThrown:** do not throw `Exception`, `RuntimeException`, `Throwable`, `Error`

### Naming
- **BooleanPropertyNaming:** booleans start with `is`, `has`, `are`, `can`
- **ClassNaming:** classes/objects use PascalCase
- **ConstructorParameterNaming:** constructor params use camelCase
- **EnumNaming:** enum entries use upper/camel enum style
- **FunctionMinLength:** function names must be at least 2 chars
- **FunctionNaming:** functions use camelCase; composables may be ignored by base rule
- **FunctionParameterNaming:** function params use camelCase
- **MemberNameEqualsClassName:** member name must not equal class name
- **NonBooleanPropertyPrefixedWithIs:** `is*` names only for booleans
- **ObjectPropertyNaming:** object properties follow configured case; prefer camelCase or UPPER_SNAKE_CASE
- **PackageNaming:** packages are lowercase hierarchical names
- **TopLevelPropertyNaming:** top-level constants UPPER_SNAKE_CASE; normal vals camelCase / Compose-approved PascalCase
- **VariableMinLength:** local names may be short but should still be meaningful
- **VariableNaming:** variables use camelCase

### Performance
- **ArrayPrimitive:** use `IntArray`, `LongArray`, etc. instead of `Array<Int>`
- **CouldBeSequence:** use sequences for long collection chains
- **ForEachOnRange:** use `for` instead of range `forEach`
- **SpreadOperator:** avoid `*array` on existing arrays
- **UnnecessaryPartOfBinaryExpression:** remove duplicated or pointless boolean parts
- **UnnecessaryTemporaryInstantiation:** avoid temporary boxed wrapper objects for conversions

### Potential bugs
- **AvoidReferentialEquality:** use structural equality for Strings
- **DontDowncastCollectionTypes:** never cast immutable collection to mutable type
- **DoubleMutabilityForCollection:** avoid `var mutableList`, prefer `val mutableList` or immutable `var`
- **ElseCaseInsteadOfExhaustiveWhen:** exhaustive `when` on enum/sealed/boolean without `else`
- **EqualsAlwaysReturnsTrueOrFalse:** `equals` must not return constant value
- **EqualsWithHashCodeExist:** if overriding `equals`, override `hashCode`
- **ExitOutsideMain:** no process-exit calls outside `main`
- **ExplicitGarbageCollectionCall:** never call GC explicitly
- **HasPlatformType:** declare platform types explicitly in API/return types
- **IgnoredReturnValue:** do not ignore important returned values
- **ImplicitDefaultLocale:** always pass `Locale` for formatting and case conversion when machine-readable
- **InvalidRange:** avoid empty or impossible ranges
- **IteratorHasNextCallsNextMethod:** `hasNext()` must be side-effect free
- **IteratorNotThrowingNoSuchElementException:** `next()` must throw when exhausted
- **LateinitUsage:** avoid `lateinit`
- **MapGetWithNotNullAssertionOperator:** never use `map[key]!!`
- **MissingPackageDeclaration:** always declare package
- **NullCheckOnMutableProperty:** snapshot mutable nullable properties before using
- **UnconditionalJumpStatementInLoop:** do not write loops that always break/continue immediately
- **UnnecessaryNotNullCheck:** remove pointless `checkNotNull` / `requireNotNull`
- **UnnecessaryNotNullOperator:** remove needless `!!`
- **UnnecessarySafeCall:** remove needless `?.`
- **UnreachableCatchBlock:** catch specific exceptions before broader ones
- **UnreachableCode:** remove dead code
- **UnsafeCallOnNullableType:** never dereference nullable values unsafely
- **UnsafeCast:** do not write impossible casts
- **UnusedUnaryOperator:** do not accidentally split arithmetic into useless unary operators
- **UselessPostfixExpression:** do not write `i = i++` / similar broken postfix code
- **WrongEqualsTypeParameter:** `equals` must take `Any?`

### Style
- **AlsoCouldBeApply:** use `apply` when the block only mutates the receiver
- **BracesOnIfStatements:** single-line braces only when necessary; multi-line braces always
- **BracesOnWhenStatements:** single/multi-line `when` braces only when necessary
- **CanBeNonNullable:** remove nullability when it serves no purpose
- **CascadingCallWrapping:** wrap full call chains consistently once wrapped
- **ClassOrdering:** properties/init -> secondary constructors -> methods -> companion object
- **DataClassContainsFunctions:** data classes should mostly only hold data and conversion helpers
- **DataClassShouldBeImmutable:** prefer `val` in data classes
- **DestructuringDeclarationWithTooManyEntries:** keep destructuring <= 5 entries
- **DoubleNegativeLambda:** avoid negative lambda inside negative function names
- **EqualsNullCall:** use `== null`, not `equals(null)`
- **EqualsOnSignatureLine:** keep `=` on the function signature line for expression bodies
- **ExplicitCollectionElementAccessMethod:** prefer `[]` over `.get()` / `.put()`
- **ExplicitItLambdaParameter:** do not write trivial explicit `it`
- **ExpressionBodySyntax:** use expression bodies for single-return functions
- **ForbiddenAnnotation:** use Kotlin / androidx annotations, not forbidden Java ones
- **ForbiddenComment:** never generate TODO/FIXME/STOPSHIP
- **ForbiddenMethodCall:** never use `print` / `println`
- **ForbiddenVoid:** use `Unit`, not `Void`
- **FunctionOnlyReturningConstant:** use constants, not constant-returning functions
- **LoopWithTooManyJumpStatements:** simplify loops with multiple jumps
- **MagicNumber:** extract numeric literals into named constants
- **MaxChainedCallsOnSameLine:** avoid very long one-line chains
- **MaxLineLength:** keep lines <= 120
- **MayBeConst:** use `const val` when eligible
- **ModifierOrder:** keep standard Kotlin modifier order
- **MultilineLambdaItParameter:** multiline lambdas should use meaningful param names
- **MultilineRawStringIndentation:** indent raw strings consistently
- **NestedClassesVisibility:** nested classes should not have misleading wider visibility
- **NewLineAtEndOfFile:** file must end with newline
- **NoTabs:** never use tabs
- **NullableBooleanCheck:** use `== true` / `!= false` rather than elvis for nullable booleans
- **ObjectLiteralToLambda:** use lambda for SAM where possible
- **OptionalAbstractKeyword:** remove redundant `abstract`
- **OptionalUnit:** omit explicit `Unit` when unnecessary
- **PreferToOverPairSyntax:** use `a to b` over `Pair(a, b)`
- **ProtectedMemberInFinalClass:** final classes should not contain protected members
- **RedundantHigherOrderMapUsage:** remove `map { it }`, prefer `onEach`, `toList`, etc.
- **RedundantVisibilityModifierRule:** remove redundant visibility modifiers
- **ReturnCount:** keep returns <= 4 and favor expression-style returns
- **SafeCast:** prefer `as?` when a cast may fail
- **SerialVersionUIDInSerializableClass:** define private const `serialVersionUID`
- **SpacingBetweenPackageAndImports:** keep correct blank lines around imports
- **StringShouldBeRawString:** use raw strings when escaping gets noisy
- **ThrowsCount:** keep throw sites <= 3
- **TrailingWhitespace:** remove trailing whitespace
- **UnderscoresInNumericLiterals:** use underscores in long numeric literals
- **UnnecessaryAbstractClass:** do not use abstract class when class/interface fits better
- **UnnecessaryAnnotationUseSiteTarget:** remove unnecessary `@field:` / `@param:` / `@property:`
- **UnnecessaryApply:** remove trivial `apply`
- **UnnecessaryBackticks:** avoid backticks unless required
- **UnnecessaryBracesAroundTrailingLambda:** use trailing lambda syntax
- **UnnecessaryFilter:** replace filter+count/isEmpty with direct operators
- **UnnecessaryInheritance:** do not inherit from `Any` / `Object`
- **UnnecessaryInnerClass:** use nested class when outer reference is not needed
- **UnnecessaryLet:** remove trivial/non-null `let`
- **UnnecessaryParentheses:** remove unnecessary parentheses
- **UntilInsteadOfRangeTo:** prefer `until` or open-ended range style when upper bound is excluded
- **UnusedImports:** remove unused imports
- **UnusedPrivateClass:** remove unused private classes
- **UnusedPrivateMember:** remove unused private members or name intentional ones `ignored` / `_`
- **UnusedPrivateProperty:** remove unused private properties
- **UseAnyOrNoneInsteadOfFind:** use `any` / `none` instead of `find() != null`
- **UseArrayLiteralsInAnnotations:** use array literal syntax in annotations
- **UseCheckNotNull:** use `checkNotNull(x)` instead of `check(x != null)`
- **UseCheckOrError:** use `check` / `error` instead of manual `IllegalStateException`
- **UseDataClass:** convert pure data holders to `data class`
- **UseEmptyCounterpart:** prefer `emptyList()` / `emptyMap()` / etc.
- **UseIfEmptyOrIfBlank:** prefer `ifEmpty` / `ifBlank`
- **UseIfInsteadOfWhen:** for simple binary conditions prefer `if` over `when`
- **UseIsNullOrEmpty:** prefer `isNullOrEmpty()`
- **UseOrEmpty:** prefer `.orEmpty()`
- **UseLet:** use `?.let` for null-dependent expressions when it clarifies flow
- **UseRequire:** use `require` / `requireNotNull` instead of manual `IllegalArgumentException`
- **UseRequireNotNull:** use `requireNotNull(x)` instead of `require(x != null)`
- **UseSumOfInsteadOfFlatMapSize:** prefer `sumOf`
- **UselessCallOnNotNull:** remove null-handling helpers on non-null values
- **UtilityClassWithPublicConstructor:** use `object` or private constructor for utility holder
- **VarCouldBeVal:** use `val` whenever reassignment is not needed
- **WildcardImport:** avoid wildcard imports except allowed package

### Formatting
- **AnnotationOnSeparateLine:** keep annotations on separate lines
- **AnnotationSpacing:** correct spacing after annotations
- **ArgumentListWrapping:** wrap long/multiline arguments cleanly
- **BackingPropertyNaming:** backing properties use underscore style where appropriate
- **BlankLineBeforeDeclaration:** insert blank lines before declarations
- **BlankLineBetweenWhenConditions:** keep `when` conditions visually separated when multiline
- **BlockCommentInitialStarAlignment:** align block comment stars correctly
- **ChainMethodContinuation:** wrap method chains consistently
- **ChainWrapping:** each wrapped chain element gets its own line
- **ClassName:** keep class names formatting-compliant
- **ClassSignature:** wrap class signatures early and keep them short
- **CommentSpacing:** proper spacing in comments
- **CommentWrapping:** wrap comments cleanly
- **ConditionWrapping:** wrap long conditions clearly
- **EnumEntryNameCase:** enum entry case must be compliant
- **EnumWrapping:** wrap enum entries consistently
- **FinalNewline:** end file with newline
- **FunKeywordSpacing:** correct spacing after `fun`
- **FunctionExpressionBody:** expression bodies formatted correctly
- **FunctionLiteral:** lambdas formatted correctly
- **FunctionReturnTypeSpacing:** correct spacing before/after return types
- **FunctionSignature:** use multiline signatures, especially from 2 params upward
- **FunctionStartOfBodySpacing:** correct spacing before function body
- **FunctionTypeModifierSpacing:** correct spacing around function type modifiers
- **FunctionTypeReferenceSpacing:** correct spacing in function types
- **IfElseWrapping:** wrap multiline if/else consistently
- **ImportOrdering:** keep import order stable
- **Indentation:** use 4 spaces consistently
- **Kdoc:** keep KDoc structure valid when present
- **KdocWrapping:** wrap KDoc properly
- **MaximumLineLength:** keep to 120 max
- **MixedConditionOperators:** parenthesize or simplify mixed `&&` / `||` conditions for clarity
- **ModifierListSpacing:** proper spacing in modifier lists
- **ModifierOrdering:** standard Kotlin modifier order
- **MultilineLoop:** wrap multiline loops correctly
- **NoBlankLineBeforeRbrace:** no blank line before `}`
- **NoBlankLineInList:** no empty lines inside arg/param lists
- **NoBlankLinesInChainedMethodCalls:** no empty lines inside chains
- **NoConsecutiveBlankLines:** no stacked blank lines
- **NoConsecutiveComments:** avoid consecutive comment blocks when unnecessary
- **NoEmptyClassBody:** use compact empty-body syntax or remove empty class
- **NoEmptyFile:** do not create empty files
- **NoEmptyFirstLineInMethodBlock:** no leading empty line in blocks
- **NoLineBreakAfterElse:** do not break immediately after `else`
- **NoLineBreakBeforeAssignment:** keep assignment on correct line
- **NoMultipleSpaces:** no alignment via extra spaces
- **NoSemicolons:** never use semicolons
- **NoSingleLineBlockComment:** prefer `//` over one-line `/* */`
- **NoTrailingSpaces:** remove trailing spaces
- **NoUnitReturn:** do not spell out unnecessary `Unit`
- **NoUnusedImports:** remove unused imports
- **NoWildcardImports:** no wildcard imports
- **NullableTypeSpacing:** proper spacing around `?`
- **PackageName:** package declarations must be formatted correctly
- **ParameterListSpacing:** correct spacing in parameter lists
- **ParameterListWrapping:** wrap parameters cleanly
- **SpacingAroundAngleBrackets:** proper generic type spacing
- **SpacingAroundColon:** proper colon spacing
- **SpacingAroundComma:** proper comma spacing
- **SpacingAroundCurly:** proper brace spacing
- **SpacingAroundDot:** no spaces around `.`
- **SpacingAroundDoubleColon:** proper `::` spacing
- **SpacingAroundKeyword:** proper spaces around keywords
- **SpacingAroundOperators:** proper operator spacing
- **SpacingAroundParens:** proper parenthesis spacing
- **SpacingAroundRangeOperator:** proper range operator spacing
- **SpacingAroundUnaryOperator:** proper unary operator spacing
- **SpacingBetweenDeclarationsWithAnnotations:** blank lines around annotated declarations
- **SpacingBetweenDeclarationsWithComments:** blank lines around comment-separated declarations
- **SpacingBetweenFunctionNameAndOpeningParenthesis:** no extra spaces
- **StatementWrapping:** wrap long statements cleanly
- **StringTemplate:** correct string-template formatting
- **StringTemplateIndent:** indent multiline templates correctly
- **TrailingCommaOnCallSite:** multiline call sites use trailing commas
- **TrailingCommaOnDeclarationSite:** multiline declarations use trailing commas
- **TryCatchFinallySpacing:** correct spacing in try/catch/finally
- **TypeArgumentListSpacing:** proper generic arg spacing
- **TypeParameterListSpacing:** proper type parameter spacing
- **UnnecessaryParenthesesBeforeTrailingLambda:** use trailing lambda form
- **ValueArgumentComment:** avoid malformed value argument comments
- **ValueParameterComment:** avoid malformed value parameter comments
- **Wrapping:** obey general wrapping contract

### Compose plugin rules
- **ComposableAnnotationNaming:** composable-related annotations follow expected naming
- **ComposableNaming:** Unit composables PascalCase, value composables camelCase
- **ComposableParamOrder:** required -> modifier -> optional -> trailing content
- **CompositionLocalAllowlist:** avoid arbitrary CompositionLocals; use allowlisted ones only
- **CompositionLocalNaming:** CompositionLocals must start with `Local`
- **ContentEmitterReturningValues:** composables either emit content or return a value, not both
- **DefaultsVisibility:** `Defaults` object visibility should match composable visibility
- **LambdaParameterInRestartableEffect:** choose effect keys and captures carefully
- **ModifierClickableOrder:** keep clickable and modifier order idiomatic
- **ModifierComposed:** avoid `Modifier.composed`
- **ModifierMissing:** expose `modifier` when component should support external layout/styling
- **ModifierNaming:** root modifier named `modifier`; sub-part modifiers use specific names
- **ModifierNotUsedAtRoot:** apply incoming modifier at root node
- **ModifierReused:** do not reuse incoming modifier on descendants
- **ModifierWithoutDefault:** `modifier` must default to `Modifier`
- **MultipleEmitters:** keep composables cohesive; avoid multiple unrelated emitters
- **MutableParams:** do not pass inherently mutable params to composables
- **MutableStateParam:** do not pass `MutableState<T>` to composables
- **PreviewAnnotationNaming:** preview annotations should be named clearly
- **PreviewPublic:** preview composables should be private
- **RememberMissing:** remember state created in composables
- **RememberContentMissing:** remember movable content
- **UnstableCollections:** avoid unstable collections in Compose APIs
- **ViewModelForwarding:** do not forward ViewModels down the tree
- **ViewModelInjection:** if a composable needs a ViewModel, make it an explicit/default parameter
- **ReusedModifierInstance:** same as modifier reuse prohibition
- **UnnecessaryEventHandlerParameter:** avoid needless event-handler params when state hoisting can simplify API
- **ComposableEventParameterNaming:** event params must be `onXxx` present tense
- **ComposableParametersOrdering:** keep composable parameter ordering consistent
- **ModifierHeightWithText:** prefer `heightIn` over fixed `height` for text-containing layouts
- **MissingModifierDefaultValue:** if modifier exists, default it to `Modifier`
- **PublicComposablePreview:** preview composables must not be public
- **TopLevelComposableFunctions:** composables should be top-level by default
- **ComposableFunctionName:** Unit composables PascalCase, value composables camelCase
- **ConditionCouldBeLifted:** lift outer conditions above layout containers when appropriate

---

## Final instruction for Cursor

When generating Kotlin in this project:

- prefer **stricter, cleaner, more explicit code**;
- optimize for **Detekt pass rate first**;
- do not rely on the formatter to fix structural problems;
- do not generate placeholder code, dead code, or weak APIs;
- for Compose, optimize for **stateless APIs, correct modifier usage, and explicit dependencies**.

If there is a choice between two valid implementations, choose the one that is:

1. more explicit,
2. lower in complexity,
3. more null-safe,
4. more Compose-idiomatic,
5. less likely to trigger Detekt or Compose rule plugins.
