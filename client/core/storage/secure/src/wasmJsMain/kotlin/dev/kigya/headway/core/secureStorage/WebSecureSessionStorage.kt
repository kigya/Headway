package dev.kigya.headway.core.secureStorage

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.outcomeSuspendCatchingOn
import kotlinx.browser.localStorage
import kotlinx.coroutines.CoroutineDispatcher

class WebSecureSessionStorage(
    private val ioDispatcher: CoroutineDispatcher,
) : SecureSessionStorageContract {

    override suspend fun loadPayload(): Outcome<SecureSessionStorageError, String?> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            localStorage.getItem(KEY_PAYLOAD)
        }

    override suspend fun savePayload(payload: String): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            localStorage.setItem(KEY_PAYLOAD, payload)
        }

    override suspend fun clear(): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            localStorage.removeItem(KEY_PAYLOAD)
        }
}

private const val KEY_PAYLOAD: String = "headway_session_payload"
