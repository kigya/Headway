package dev.kigya.headway.core.session.domain.repository

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.model.LocalSessionRecord

interface AuthRepositoryContract {
    suspend fun loginWithGoogle(
        idToken: String,
    ): Outcome<SessionDomainError, LocalSessionRecord.Registered>
    suspend fun loginAsGuest(): Outcome<SessionDomainError, LocalSessionRecord.Guest>
    suspend fun refreshRegisteredAccess(refreshToken: String): Outcome<SessionDomainError, String>
    suspend fun revokeGuestSession(): Outcome<SessionDomainError, Unit>
}
