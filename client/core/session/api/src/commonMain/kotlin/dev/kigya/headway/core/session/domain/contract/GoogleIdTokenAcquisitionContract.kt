package dev.kigya.headway.core.session.domain.contract

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError

fun interface GoogleIdTokenAcquisitionContract {
    suspend fun obtainIdToken(): Outcome<SessionDomainError, String>
}
