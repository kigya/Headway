package dev.kigya.headway.core.secureStorage

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.outcomeSuspendCatchingOn
import kotlinx.coroutines.CoroutineDispatcher
import platform.Foundation.NSUserDefaults

class IosSecureSessionStorage(
    private val ioDispatcher: CoroutineDispatcher,
) : SecureSessionStorageContract {

    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun loadPayload(): Outcome<SecureSessionStorageError, String?> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            defaults.stringForKey(KEY_PAYLOAD)
        }

    override suspend fun savePayload(payload: String): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            defaults.setObject(payload, KEY_PAYLOAD)
        }

    override suspend fun clear(): Outcome<SecureSessionStorageError, Unit> =
        outcomeSuspendCatchingOn(
            dispatcher = ioDispatcher,
            mapError = { SecureSessionStorageError.OperationFailed(it) },
        ) {
            defaults.removeObjectForKey(KEY_PAYLOAD)
        }
}

private const val KEY_PAYLOAD: String = "headway_session_payload"
