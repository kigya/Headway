package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.GuestLearningRepositoryContract
import dev.kigya.headway.core.session.model.GuestLearningOverview

class LoadGuestLearningOverviewUseCase(
    private val guestLearningRepository: GuestLearningRepositoryContract,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, GuestLearningOverview> =
        guestLearningRepository.loadGuestLearningOverview()
}
