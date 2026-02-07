package dev.kigya.headway.auth.internal.domain.error

sealed class AuthException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {

    data class InvalidRequest(
        override val message: String,
        override val cause: Throwable? = null,
    ) : AuthException(message, cause)

    data class Unauthorized(
        override val message: String = "Unauthorized",
        override val cause: Throwable? = null,
    ) : AuthException(message, cause)

    open class Forbidden(
        override val message: String = "Forbidden",
        override val cause: Throwable? = null,
    ) : AuthException(message, cause)

    data class UserNotInvited(
        override val message: String = "User is not invited",
    ) : Forbidden(message)

    data class UserNotActive(
        override val message: String = "User is not active",
    ) : Forbidden(message)

    data class DependencyUnavailable(
        val dependency: String,
        override val message: String = "Dependency unavailable: $dependency",
        override val cause: Throwable? = null,
    ) : AuthException(message, cause)

    class UpstreamProtocol(
        val dependency: String,
        val status: Int,
        override val message: String = "Unexpected response from $dependency: $status",
        override val cause: Throwable? = null,
    ) : AuthException(message, cause)
}
