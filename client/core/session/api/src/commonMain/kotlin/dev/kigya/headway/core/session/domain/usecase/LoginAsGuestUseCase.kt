package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.AuthRepositoryContract
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract

class LoginAsGuestUseCase(
    private val authRepository: AuthRepositoryContract,
    private val persistence: LocalSessionPersistenceContract,
    private val accessTokenStore: HeadwayAccessTokenStore,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, Unit> =
        when (val guest = authRepository.loginAsGuest()) {
            is Outcome.Success -> {
                accessTokenStore.update(guest.value.accessToken)
                when (val saved = persistence.saveRecord(guest.value)) {
                    is Outcome.Success -> Outcome.success(Unit)
                    is Outcome.Failure -> saved
                }
            }

            is Outcome.Failure -> guest
        }
}
