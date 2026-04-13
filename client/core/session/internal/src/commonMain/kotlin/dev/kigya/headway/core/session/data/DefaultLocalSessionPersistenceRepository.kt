package dev.kigya.headway.core.session.data

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.outcomeSuspendCatching
import dev.kigya.headway.core.secureStorage.SecureSessionStorageContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract
import dev.kigya.headway.core.session.model.LocalSessionRecord
import kotlinx.serialization.json.Json

internal class DefaultLocalSessionPersistenceRepository(
    private val secureStorage: SecureSessionStorageContract,
) : LocalSessionPersistenceContract {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    override suspend fun loadRecord(): Outcome<SessionDomainError, LocalSessionRecord?> =
        when (val loaded = secureStorage.loadPayload()) {
            is Outcome.Failure -> Outcome.failure(SessionDomainError.LocalPersistenceFailed)
            is Outcome.Success -> outcomeSuspendCatching(
                mapError = { SessionDomainError.LocalPersistenceFailed },
            ) {
                val raw = loaded.value ?: return@outcomeSuspendCatching null
                json.decodeFromString<LocalSessionRecord>(raw)
            }
        }

    override suspend fun saveRecord(record: LocalSessionRecord): Outcome<SessionDomainError, Unit> =
        when (
            val encoded = outcomeSuspendCatching(
                mapError = { SessionDomainError.LocalPersistenceFailed },
            ) {
                json.encodeToString(LocalSessionRecord.serializer(), record)
            }
        ) {
            is Outcome.Failure -> Outcome.failure(encoded.error)
            is Outcome.Success -> when (secureStorage.savePayload(encoded.value)) {
                is Outcome.Failure -> Outcome.failure(SessionDomainError.LocalPersistenceFailed)
                is Outcome.Success -> Outcome.success(Unit)
            }
        }

    override suspend fun clearRecord(): Outcome<SessionDomainError, Unit> =
        when (secureStorage.clear()) {
            is Outcome.Failure -> Outcome.failure(SessionDomainError.LocalPersistenceFailed)
            is Outcome.Success -> Outcome.success(Unit)
        }
}
