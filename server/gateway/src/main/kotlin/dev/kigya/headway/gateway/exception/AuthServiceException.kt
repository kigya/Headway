package dev.kigya.headway.gateway.exception

internal class AuthServiceException(
    val code: GatewayErrorCode,
    cause: Throwable? = null,
) : RuntimeException(null, cause)

internal enum class GatewayErrorCode {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    DEPENDENCY_UNAVAILABLE,
    INTERNAL,
}
