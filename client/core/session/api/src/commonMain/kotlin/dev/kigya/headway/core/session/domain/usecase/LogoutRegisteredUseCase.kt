package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract

class LogoutRegisteredUseCase(
    private val persistence: LocalSessionPersistenceContract,
    private val accessTokenStore: HeadwayAccessTokenStore,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, Unit> {
        accessTokenStore.update(null)
        return persistence.clearRecord()
    }
}
