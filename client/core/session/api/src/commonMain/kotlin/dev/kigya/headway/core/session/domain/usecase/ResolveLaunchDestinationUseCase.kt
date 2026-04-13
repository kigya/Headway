package dev.kigya.headway.core.session.domain.usecase

import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.outcome.getOrElse
import dev.kigya.headway.core.outcome.getOrNull
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.repository.AuthRepositoryContract
import dev.kigya.headway.core.session.domain.repository.LocalSessionPersistenceContract
import dev.kigya.headway.core.session.domain.repository.ProtectedRepositoryContract
import dev.kigya.headway.core.session.model.LaunchDestination
import dev.kigya.headway.core.session.model.LocalSessionRecord

class ResolveLaunchDestinationUseCase(
    private val persistence: LocalSessionPersistenceContract,
    private val protectedRepository: ProtectedRepositoryContract,
    private val authRepository: AuthRepositoryContract,
    private val accessTokenStore: HeadwayAccessTokenStore,
    private val clock: SessionClock,
) {

    suspend operator fun invoke(): Outcome<SessionDomainError, LaunchDestination> {
        val record = persistence.loadRecord().getOrElse { return Outcome.success(LaunchDestination.Auth) }
            ?: return Outcome.success(LaunchDestination.Auth)

        return when (record) {
            is LocalSessionRecord.Guest -> resolveGuest(record)
            is LocalSessionRecord.Registered -> resolveRegistered(record)
        }
    }

    private suspend fun resolveGuest(
        record: LocalSessionRecord.Guest,
    ): Outcome<SessionDomainError, LaunchDestination> {
        if (clock.nowEpochMillis() >= record.expiresAtEpochMs) {
            persistence.clearRecord()
            accessTokenStore.update(null)
            return Outcome.success(LaunchDestination.Auth)
        }
        accessTokenStore.update(record.accessToken)
        return when (val overview = protectedRepository.loadGuestLearningOverview()) {
            is Outcome.Success -> Outcome.success(LaunchDestination.LearnQuestions)
            is Outcome.Failure -> guestFailure(overview.error)
        }
    }

    private suspend fun guestFailure(
        error: SessionDomainError,
    ): Outcome<SessionDomainError, LaunchDestination> {
        if (error == SessionDomainError.NetworkUnavailable ||
            error == SessionDomainError.DependencyUnavailable
        ) {
            return Outcome.success(LaunchDestination.Auth)
        }
        persistence.clearRecord()
        accessTokenStore.update(null)
        return Outcome.success(LaunchDestination.Auth)
    }

    private suspend fun resolveRegistered(
        record: LocalSessionRecord.Registered,
    ): Outcome<SessionDomainError, LaunchDestination> {
        accessTokenStore.update(record.accessToken)
        return when (val home = protectedRepository.loadHomeSummary()) {
            is Outcome.Success -> Outcome.success(LaunchDestination.Home)
            is Outcome.Failure -> registeredHomeFailure(record, home.error)
        }
    }

    private suspend fun registeredHomeFailure(
        record: LocalSessionRecord.Registered,
        error: SessionDomainError,
    ): Outcome<SessionDomainError, LaunchDestination> {
        when (error) {
            SessionDomainError.RegisteredSessionInvalid -> {
                val newAccess = authRepository.refreshRegisteredAccess(record.refreshToken).getOrNull()
                if (newAccess == null) {
                    persistence.clearRecord()
                    accessTokenStore.update(null)
                    return Outcome.success(LaunchDestination.Auth)
                }
                val updated = record.copy(accessToken = newAccess)
                persistence.saveRecord(updated)
                accessTokenStore.update(newAccess)
                return when (protectedRepository.loadHomeSummary()) {
                    is Outcome.Success -> Outcome.success(LaunchDestination.Home)
                    is Outcome.Failure -> {
                        persistence.clearRecord()
                        accessTokenStore.update(null)
                        Outcome.success(LaunchDestination.Auth)
                    }
                }
            }
            SessionDomainError.NetworkUnavailable,
            SessionDomainError.DependencyUnavailable,
            -> return Outcome.success(LaunchDestination.Auth)
            SessionDomainError.AuthorizationDenied,
            SessionDomainError.UserNotInvited,
            SessionDomainError.GuestSessionInvalid,
            SessionDomainError.Unexpected,
            SessionDomainError.ValidationFailed,
            SessionDomainError.Conflict,
            SessionDomainError.LocalPersistenceFailed,
            SessionDomainError.GoogleSignInCancelled,
            SessionDomainError.GoogleSignInUnavailable,
            -> {
                persistence.clearRecord()
                accessTokenStore.update(null)
                return Outcome.success(LaunchDestination.Auth)
            }
        }
    }
}
