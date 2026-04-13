package dev.kigya.headway.core.session.domain.error

import dev.kigya.headway.core.network.api.HeadwayRemoteGraphqlFailure

internal fun mapHeadwayGraphqlFailureToSessionError(
    failure: HeadwayRemoteGraphqlFailure,
): SessionDomainError = when (failure) {
    HeadwayRemoteGraphqlFailure.Network -> SessionDomainError.NetworkUnavailable
    HeadwayRemoteGraphqlFailure.UnexpectedResponse -> SessionDomainError.Unexpected
    is HeadwayRemoteGraphqlFailure.GraphQl -> mapGraphQlFailure(failure)
}

private fun mapGraphQlFailure(failure: HeadwayRemoteGraphqlFailure.GraphQl): SessionDomainError =
    sessionErrorForGraphQlWireKey(failure.reason)
        ?: sessionErrorForGraphQlWireKey(failure.code)
        ?: sessionErrorForGraphQlCategory(failure.category)

private fun sessionErrorForGraphQlWireKey(raw: String?): SessionDomainError? =
    when (raw?.uppercase()) {
        "ACCESS_TOKEN_EXPIRED",
        "INVALID_ACCESS_TOKEN",
        "INVALID_REFRESH_TOKEN",
        "EMPTY_REFRESH_TOKEN",
        "MISSING_AUTH_HEADER",
        "MALFORMED_BEARER_HEADER",
        "EMPTY_ID_TOKEN",
        "ACCOUNT_INACTIVE",
        "UNAUTHENTICATED",
        -> SessionDomainError.RegisteredSessionInvalid
        "GUEST_TOKEN_EXPIRED",
        "INVALID_GUEST_TOKEN",
        -> SessionDomainError.GuestSessionInvalid
        "USER_NOT_INVITED",
        -> SessionDomainError.UserNotInvited
        "INSUFFICIENT_ROLE",
        "GUEST_NOT_ALLOWED",
        "GOOGLE_EMAIL_NOT_VERIFIED",
        "FORBIDDEN",
        -> SessionDomainError.AuthorizationDenied
        "DEPENDENCY_FAILURE",
        "UPSTREAM_PROTOCOL",
        "BAD_GATEWAY",
        "SERVICE_UNAVAILABLE",
        -> SessionDomainError.DependencyUnavailable
        "IDENTITY_CONFLICT",
        -> SessionDomainError.Conflict
        "BAD_REQUEST",
        "EMPTY_FINGERPRINT",
        -> SessionDomainError.ValidationFailed
        else -> null
    }

private fun sessionErrorForGraphQlCategory(category: String?): SessionDomainError =
    when (category?.lowercase()) {
        "authentication" -> SessionDomainError.RegisteredSessionInvalid
        "authorization" -> SessionDomainError.AuthorizationDenied
        "validation" -> SessionDomainError.ValidationFailed
        "conflict" -> SessionDomainError.Conflict
        "dependency" -> SessionDomainError.DependencyUnavailable
        "internal" -> SessionDomainError.Unexpected
        "not_found" -> SessionDomainError.Unexpected
        else -> SessionDomainError.Unexpected
    }
