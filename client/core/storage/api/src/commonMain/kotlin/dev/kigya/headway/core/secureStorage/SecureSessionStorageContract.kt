package dev.kigya.headway.core.secureStorage

import dev.kigya.headway.core.outcome.Outcome

interface SecureSessionStorageContract {
    suspend fun loadPayload(): Outcome<SecureSessionStorageError, String?>

    suspend fun savePayload(payload: String): Outcome<SecureSessionStorageError, Unit>

    suspend fun clear(): Outcome<SecureSessionStorageError, Unit>
}
