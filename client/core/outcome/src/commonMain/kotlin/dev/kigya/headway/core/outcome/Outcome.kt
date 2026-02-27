@file:OptIn(ExperimentalContracts::class)

package dev.kigya.headway.core.outcome

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

/**
 * A lightweight container of a computation result that can be either a [Success] with a [Value]
 * or a [Failure] with an [Error].
 */
public sealed interface Outcome<out Error, out Value> {

    /** Represents a successful outcome holding a [Value]. */
    public data class Success<Value>(val value: Value) : Outcome<Nothing, Value>

    /** Represents a failed outcome holding an [Error]. */
    public data class Failure<Error>(val error: Error) : Outcome<Error, Nothing>

    public companion object {
        /** Wraps a successful [value] in an [Outcome]. */
        public fun <Value> success(value: Value): Outcome<Nothing, Value> = Success(value)

        /** Wraps an [error] in an [Outcome]. */
        public fun <Error> failure(error: Error): Outcome<Error, Nothing> = Failure(error)
    }
}

// ─── Retrieval helpers ──────────────────────────────────────────────────────

/** Returns the [Value] if this is [Success], or `null` otherwise. */
public fun <Error, Value> Outcome<Error, Value>.getOrNull(): Value? =
    (this as? Outcome.Success)?.value

/** Returns the [Error] if this is [Failure], or `null` otherwise. */
public fun <Error, Value> Outcome<Error, Value>.errorOrNull(): Error? =
    (this as? Outcome.Failure)?.error

/**
 * Returns the [Value] if [Outcome.Success], or computes an alternative via [onFailure].
 *
 * ```kotlin
 * // Define your error model
 * sealed interface AppError {
 *   object NotFound: AppError
 *   object Timeout: AppError
 * }
 *
 * // Success example
 * val ok: Outcome<AppError, String> = Outcome.success("Hello")
 * val greeting = ok.getOrElse { error ->
 *   // This branch will never run for this instance
 *   when (error) {
 *     AppError.NotFound -> "NotFound"
 *     AppError.Timeout  -> "Timeout"
 *   }
 * }
 * println(greeting)  // Hello
 *
 * // Failure example
 * val missing: Outcome<AppError, String> = Outcome.failure(AppError.NotFound)
 * val fallback = missing.getOrElse { error ->
 *   when (error) {
 *     AppError.NotFound -> println("Resource not found")
 *     AppError.Timeout  -> println("Request timed out")
 *   }
 *   "<default>"
 * }
 * println(fallback)  // <default>
 * ```
 */
public inline fun <Error, Value> Outcome<Error, Value>.getOrElse(
    onFailure: (Error) -> Value,
): Value {
    contract { callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Outcome.Success -> value
        is Outcome.Failure -> onFailure(error)
    }
}

/**
 * Returns the [Value] if [Outcome.Success], or [defaultValue] if [Outcome.Failure].
 *
 * ```kotlin
 * val x: Outcome<String, Int> = Outcome.failure("Oops")
 * println(x.getOrDefault(0)) // 0
 *
 * val y: Outcome<String, Int> = Outcome.success(5)
 * println(y.getOrDefault(0)) // 5
 * ```
 */
public fun <Error, Value> Outcome<Error, Value>.getOrDefault(
    defaultValue: Value,
): Value = if (this is Outcome.Success) value else defaultValue

// ─── Observers ──────────────────────────────────────────────────────────────

/**
 * Executes the corresponding **side‑effect** lambda and returns the _same_ [Outcome] unchanged.
 *
 * Use it when you want to _peek_ at the result (for logging, metrics, etc.) but keep
 * piping the value through a fluent chain.
 *
 * ### Example
 * ```kotlin
 * sealed interface AuthError { object Network : AuthError; object Invalid : AuthError }
 *
 * val outcome: Outcome<AuthError, String> = authenticateUser()
 *
 * outcome
 *   .handle(
 *     onFailure = { println("Fail → $it") },   // side‑effect only
 *     onSuccess = { println("Token → $it") }
 *   )
 *   .mapSuccess { token -> token.take(4) }      // chain continues
 *   // ...
 * ```
 *
 * @param onFailure action executed **only** if this is [Outcome.Failure].
 * @param onSuccess action executed **only** if this is [Outcome.Success].
 * @return the original [Outcome] instance to enable further chaining.
 */
public inline fun <Error, Value> Outcome<Error, Value>.handle(
    onFailure: (Error) -> Unit = {},
    onSuccess: (Value) -> Unit = {},
): Outcome<Error, Value> {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Outcome.Success -> {
            onSuccess(value); this
        }

        is Outcome.Failure -> {
            onFailure(error); this
        }
    }
}

/**
 * Converts an [Outcome] into a single value of type [Result] by handling both branches.
 *
 * This is the “_unwrap / fold_” operation: it **closes** the `Outcome` and produces a value
 * that your calling code can work with directly.
 *
 * ### Example
 * ```kotlin
 * sealed interface AuthError { object Network : AuthError; object Invalid : AuthError }
 *
 * val message: String = authenticateUser()
 *   .unwrap(
 *     onFailure = { error ->
 *       when (error) {
 *         AuthError.Network -> "Check your connection"
 *         AuthError.Invalid -> "Credentials are wrong"
 *       }
 *     },
 *     onSuccess = { token -> "Logged in with: $token" }
 *   )
 *
 * println(message)
 * ```
 *
 * @param onFailure mapper called if this is [Outcome.Failure].
 * @param onSuccess mapper called if this is [Outcome.Success].
 * @return whatever [onFailure] or [onSuccess] returns.
 */
public inline fun <Error, Value, Result> Outcome<Error, Value>.unwrap(
    onFailure: (Error) -> Result,
    onSuccess: (Value) -> Result,
): Result {
    contract {
        callsInPlace(onFailure, InvocationKind.AT_MOST_ONCE)
        callsInPlace(onSuccess, InvocationKind.AT_MOST_ONCE)
    }
    return when (this) {
        is Outcome.Success -> onSuccess(value)
        is Outcome.Failure -> onFailure(error)
    }
}

/**
 * Perform [action] if this is [Outcome.Success].
 *
 * ```kotlin
 * val r = Outcome.success<Nothing, String>("OK")
 * r.onSuccess { println("Got: $it") } // Got: OK
 * ```
 */
public inline fun <Error, Value> Outcome<Error, Value>.onSuccess(
    action: (Value) -> Unit,
): Outcome<Error, Value> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return apply { if (this is Outcome.Success) action(value) }
}

/**
 * Perform [action] if this is [Outcome.Failure].
 *
 * ```kotlin
 * val r = Outcome.failure<String, Int>("Fail")
 * r.onFailure { println("Error: $it") } // Error: Fail
 * ```
 */
public inline fun <Error, Value> Outcome<Error, Value>.onFailure(
    action: (Error) -> Unit,
): Outcome<Error, Value> {
    contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
    return apply { if (this is Outcome.Failure) action(error) }
}

// ─── Transformation ─────────────────────────────────────────────────────────

/**
 * Transform the successful [Value] into [NewValue], preserving any [Error].
 *
 * ```kotlin
 * val r = Outcome.success<Nothing, Int>(2)
 * val doubled = r.mapSuccess { it * 2 }
 * println(doubled) // Success(value=4)
 * ```
 */
public inline fun <Error, Value, NewValue> Outcome<Error, Value>.mapSuccess(
    transform: (Value) -> NewValue,
): Outcome<Error, NewValue> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Outcome.Success -> Outcome.success(transform(value))
        is Outcome.Failure -> this
    }
}

/**
 * Transform the [Error] into [NewError], preserving any [Value].
 *
 * ```kotlin
 * val r: Outcome<Int, String> = Outcome.failure(404)
 * val mapped = r.mapError { code -> "Error code: $code" }
 * println(mapped) // Failure(error=Error code: 404)
 * ```
 */
public inline fun <Error, Value, NewError> Outcome<Error, Value>.mapFailure(
    transform: (Error) -> NewError,
): Outcome<NewError, Value> {
    contract { callsInPlace(transform, InvocationKind.AT_MOST_ONCE) }
    return when (this) {
        is Outcome.Success -> this
        is Outcome.Failure -> Outcome.failure(transform(error))
    }
}

/**
 * Runs [fa] and [fb] in parallel and zips their results:
 *   • If both succeed, returns Success(transform(a, b))
 *   • If one fails, returns that Failure
 *   • If both fail, combines their errors via [combineError] into a single Failure
 *
 * @param ctx         coroutine context for launching the two parallel tasks
 * @param fa          first suspend producer of Outcome<Error, A>
 * @param fb          second suspend producer of Outcome<Error, B>
 * @param combineError how to merge two Error values when both fail
 * @param transform   how to combine A and B when both succeed
 *
 * @return Outcome<Error, R>
 *
 * ```
 * // Example:
 * sealed interface AppError {
 *   object FetchAError : AppError
 *   object FetchBError : AppError
 *   data class Combined(val errors: List<AppError>) : AppError
 * }
 *
 * suspend fun fetchA(): Outcome<AppError, Int> = Outcome.success(1)
 * suspend fun fetchB(): Outcome<AppError, String> = Outcome.failure(AppError.FetchBError)
 *
 * val result = zipParallelAccumulate(
 *   a = { fetchA() },
 *   b = { fetchB() },
 *   combineError = { e1, e2 -> AppError.Combined(listOf(e1, e2)) }
 * ) { a, b ->
 *   "Got $a and $b"
 * }
 *
 * // result == Outcome.Failure(AppError.FetchBError)
 * ```
 */
public suspend inline fun <Error, A, B, R> zipParallelAccumulate(
    ctx: CoroutineContext = EmptyCoroutineContext,
    noinline fa: suspend () -> Outcome<Error, A>,
    noinline fb: suspend () -> Outcome<Error, B>,
    crossinline combineError: (Error, Error) -> Error,
    crossinline transform: (A, B) -> R,
): Outcome<Error, R> {
    contract {
        callsInPlace(combineError, InvocationKind.AT_MOST_ONCE)
        callsInPlace(transform, InvocationKind.AT_MOST_ONCE)
    }
    return coroutineScope {
        val da = async(ctx) { fa() }
        val db = async(ctx) { fb() }
        val oa = da.await()
        val ob = db.await()

        when {
            oa is Outcome.Success && ob is Outcome.Success ->
                Outcome.success(transform(oa.value, ob.value))

            oa is Outcome.Failure && ob is Outcome.Failure ->
                Outcome.failure(combineError(oa.error, ob.error))

            oa is Outcome.Failure -> oa
            ob is Outcome.Failure -> ob
            else -> error("unreachable")
        }
    }
}

// ─── Exception handling ─────────────────────────────────────────────────────

/**
 * Execute [block] and wrap its result or exception in an [Outcome].
 *
 * Example:
 * ```kotlin
 * // Simple safe parse
 * val success = outcomeCatching { "123".toInt() }
 * success.onSuccess { println("Parsed: $it") }
 *        .onFailure { println("Error: ${it::class.simpleName}") }
 * // Output: Parsed: 123
 *
 * // Failure case
 * val failure = outcomeCatching { "NaN".toInt() }
 * failure.onSuccess { println("Parsed: $it") }
 *        .onFailure { println("Error: ${it::class.simpleName}") }
 * // Output: Error: NumberFormatException
 * ```
 */
public inline fun <Value> outcomeCatching(
    block: () -> Value,
): Outcome<Throwable, Value> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        Outcome.success(block())
    } catch (t: Throwable) {
        Outcome.failure(t)
    }
}

/**
 * Execute [block], mapping any exception via [mapError] into a custom error type.
 *
 * Example:
 * ```kotlin
 * sealed interface AppError {
 *   object IoError : AppError
 *   data class Other(val cause: Throwable) : AppError
 * }
 *
 * val result = outcomeCatching(
 *   mapError = { t ->
 *     if (t is IOException) AppError.IoError else AppError.Other(t)
 *   }
 * ) {
 *   throw IOException("Disk full")
 * }
 *
 * result.onFailure { println("Mapped error: $it") }
 * // Output: Mapped error: IoError
 * ```
 */
public inline fun <Error, Value> outcomeCatching(
    mapError: (Throwable) -> Error,
    block: () -> Value,
): Outcome<Error, Value> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        callsInPlace(mapError, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        Outcome.success(block())
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        Outcome.failure(mapError(t))
    }
}

/**
 * Execute suspend [block] and wrap its result or exception in an [Outcome].
 *
 * Example:
 * ```kotlin
 * suspend fun mayTimeout(): String {
 *   throw TimeoutCancellationException("timed out")
 * }
 *
 * val r = runBlocking {
 *   outcomeSuspendCatching { mayTimeout() }
 * }
 *
 * r.onFailure { println("Timeout: ${it.message}") }
 * // Output: Timeout: timed out
 * ```
 */
public suspend inline fun <Value> outcomeSuspendCatching(
    block: suspend () -> Value,
): Outcome<Throwable, Value> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        Outcome.success(block())
    } catch (t: TimeoutCancellationException) {
        Outcome.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        Outcome.failure(t)
    }
}

/**
 * Execute suspend [block] and map exceptions via [mapError] into a custom error.
 *
 * Example:
 * ```kotlin
 * sealed interface AppError { object Timeout : AppError; data class Other(val t: Throwable) : AppError }
 *
 * runBlocking {
 *   val r = outcomeSuspendCatching(
 *     mapError = { t -> if (t is TimeoutCancellationException) AppError.Timeout else AppError.Other(t) }
 *   ) {
 *     error("boom")
 *   }
 *   r.onFailure { println("Mapped: $it") }
 *   // Output: Mapped: Other(java.lang.IllegalStateException: boom)
 * }
 * ```
 */
public suspend inline fun <Error, Value> outcomeSuspendCatching(
    crossinline mapError: (Throwable) -> Error,
    crossinline block: suspend () -> Value,
): Outcome<Error, Value> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        callsInPlace(mapError, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        Outcome.success(block())
    } catch (t: TimeoutCancellationException) {
        Outcome.failure(mapError(t))
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        Outcome.failure(mapError(t))
    }
}

/**
 * Execute suspend [block] on [dispatcher] and wrap its result or exception in an [Outcome].
 *
 * Example:
 * ```kotlin
 * fun heavyComputation(): Int = 7 * 6
 *
 * runBlocking {
 *   val r = outcomeSuspendCatchingOn(Dispatchers.Default) { heavyComputation() }
 *   r.onSuccess { println("Result: $it") }
 *   // Output: Result: 42
 * }
 * ```
 */
public suspend inline fun <Value> outcomeSuspendCatchingOn(
    dispatcher: CoroutineDispatcher,
    crossinline block: suspend () -> Value,
): Outcome<Throwable, Value> {
    contract { callsInPlace(block, InvocationKind.AT_MOST_ONCE) }
    return try {
        val r = withContext(dispatcher) { block() }
        Outcome.success(r)
    } catch (t: TimeoutCancellationException) {
        Outcome.failure(t)
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        Outcome.failure(t)
    }
}

/**
 * Execute suspend [block] on [dispatcher] and map exceptions via [mapError] into a custom error.
 *
 * Example:
 * ```kotlin
 * sealed interface AppError { data class Other(val t: Throwable) : AppError }
 *
 * runBlocking {
 *   val r = outcomeSuspendCatchingOn(
 *     Dispatchers.IO,
 *     mapError = { t -> AppError.Other(t) }
 *   ) { error("fail") }
 *
 *   r.onFailure { println("Got: $it") }
 *   // Output: Got: Other(java.lang.IllegalStateException: fail)
 * }
 * ```
 */
public suspend inline fun <Error, Value> outcomeSuspendCatchingOn(
    dispatcher: CoroutineDispatcher,
    crossinline mapError: (Throwable) -> Error,
    crossinline block: suspend () -> Value,
): Outcome<Error, Value> {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
        callsInPlace(mapError, InvocationKind.AT_MOST_ONCE)
    }
    return try {
        val r = withContext(dispatcher) { block() }
        Outcome.success(r)
    } catch (t: TimeoutCancellationException) {
        Outcome.failure(mapError(t))
    } catch (c: CancellationException) {
        throw c
    } catch (t: Throwable) {
        Outcome.failure(mapError(t))
    }
}
