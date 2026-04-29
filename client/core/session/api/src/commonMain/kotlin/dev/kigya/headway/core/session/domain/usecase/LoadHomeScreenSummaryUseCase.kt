package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.HomeRepositoryContract
import dev.kigya.headway.core.session.model.HomeScreenSummary

class LoadHomeScreenSummaryUseCase(
    private val homeRepository: HomeRepositoryContract,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, HomeScreenSummary> =
        homeRepository.loadHomeSummary()
}
