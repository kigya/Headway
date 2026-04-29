package dev.kigya.headway.core.session.domain.repository

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.model.GuestLearningOverview

interface GuestLearningRepositoryContract {
    suspend fun loadGuestLearningOverview(): Outcome<SessionDomainError, GuestLearningOverview>
}
