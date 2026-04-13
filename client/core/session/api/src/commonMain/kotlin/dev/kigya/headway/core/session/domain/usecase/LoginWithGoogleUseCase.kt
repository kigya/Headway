package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.AuthRepositoryContract
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract

class LoginWithGoogleUseCase(
    private val authRepository: AuthRepositoryContract,
    private val persistence: LocalSessionPersistenceContract,
    private val accessTokenStore: HeadwayAccessTokenStore,
) {

    suspend operator fun invoke(idToken: String): Outcome<SessionDomainError, Unit> =
        when (val registered = authRepository.loginWithGoogle(idToken)) {
            is Outcome.Success ->
                when (val saved = persistence.saveRecord(registered.value)) {
                    is Outcome.Success -> {
                        accessTokenStore.update(registered.value.accessToken)
                        Outcome.success(Unit)
                    }

                    is Outcome.Failure -> saved
                }

            is Outcome.Failure -> registered
        }
}
