package dev.kigya.headway.gateway.core.exception

internal enum class GatewayErrorCode {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    DEPENDENCY_UNAVAILABLE,
    INTERNAL,
}

internal sealed class GatewayException(
    val code: GatewayErrorCode,
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {

    class InvalidRequest(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.BAD_REQUEST, message, cause)

    class Unauthorized(
        override val message: String = "Unauthorized",
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.UNAUTHORIZED, message, cause)

    open class Forbidden(
        override val message: String = "Forbidden",
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.FORBIDDEN, message, cause)

    class DependencyUnavailable(
        val dependency: String,
        override val message: String = "Dependency unavailable: $dependency",
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.DEPENDENCY_UNAVAILABLE, message, cause)

    class UpstreamProtocol(
        val dependency: String,
        val status: Int,
        override val message: String = "Unexpected response from $dependency: $status",
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.DEPENDENCY_UNAVAILABLE, message, cause)

    class Internal(
        override val message: String = "Internal server error",
        override val cause: Throwable? = null,
    ) : GatewayException(GatewayErrorCode.INTERNAL, message, cause)
}
