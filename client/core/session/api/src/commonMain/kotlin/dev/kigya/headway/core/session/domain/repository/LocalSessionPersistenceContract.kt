package dev.kigya.headway.core.session.domain.repository

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.model.LocalSessionRecord

interface LocalSessionPersistenceContract {
    suspend fun loadRecord(): Outcome<SessionDomainError, LocalSessionRecord?>
    suspend fun saveRecord(record: LocalSessionRecord): Outcome<SessionDomainError, Unit>
    suspend fun clearRecord(): Outcome<SessionDomainError, Unit>
}
