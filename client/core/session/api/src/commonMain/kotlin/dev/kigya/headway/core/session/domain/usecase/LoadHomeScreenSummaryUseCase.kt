package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.ProtectedRepositoryContract
import dev.kigya.headway.core.session.model.HomeScreenSummary

class LoadHomeScreenSummaryUseCase(
    private val protectedRepository: ProtectedRepositoryContract,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, HomeScreenSummary> =
        protectedRepository.loadHomeSummary()
}
