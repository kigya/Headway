package dev.kigya.headway.core.session.domain.error

sealed interface SessionDomainError {
    data object NetworkUnavailable : SessionDomainError
    data object Unexpected : SessionDomainError
    data object RegisteredSessionInvalid : SessionDomainError
    data object GuestSessionInvalid : SessionDomainError
    data object UserNotInvited : SessionDomainError
    data object AuthorizationDenied : SessionDomainError
    data object ValidationFailed : SessionDomainError
    data object Conflict : SessionDomainError
    data object DependencyUnavailable : SessionDomainError
    data object LocalPersistenceFailed : SessionDomainError
    data object GoogleSignInCancelled : SessionDomainError
    data object GoogleSignInUnavailable : SessionDomainError
}
