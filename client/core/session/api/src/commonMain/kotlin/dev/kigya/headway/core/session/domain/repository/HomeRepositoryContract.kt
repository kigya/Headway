package dev.kigya.headway.core.session.domain.repository

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.model.HomeScreenSummary

interface HomeRepositoryContract {
    suspend fun loadHomeSummary(): Outcome<SessionDomainError, HomeScreenSummary>
}
