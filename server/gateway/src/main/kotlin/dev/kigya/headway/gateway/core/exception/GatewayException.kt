package dev.kigya.headway.gateway.core.exception

import io.ktor.http.HttpStatusCode

internal enum class GatewayErrorCode(val rawName: String) {
    BAD_REQUEST("Bad Request"),
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    NOT_FOUND("Not Found"),
    CONFLICT("Conflict"),
    DEPENDENCY_UNAVAILABLE("Dependency Unavailable"),
    INTERNAL("Internal Server Error"),
}

internal sealed class GatewayException(
    val code: GatewayErrorCode,
    val httpStatus: Int,
    override val message: String,
    open val dependency: String? = null,
    open val upstreamStatus: Int? = null,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {

    class InvalidRequest(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.BAD_REQUEST,
        httpStatus = HttpStatusCode.BadRequest.value,
        message = message,
        cause = cause,
    )

    class Unauthorized(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.UNAUTHORIZED,
        httpStatus = HttpStatusCode.Unauthorized.value,
        message = message,
        cause = cause,
    )

    open class Forbidden(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.FORBIDDEN,
        httpStatus = HttpStatusCode.Forbidden.value,
        message = message,
        cause = cause,
    )

    class NotFound(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.NOT_FOUND,
        httpStatus = HttpStatusCode.NotFound.value,
        message = message,
        cause = cause,
    )

    open class Conflict(
        override val message: String,
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.CONFLICT,
        httpStatus = HttpStatusCode.Conflict.value,
        message = message,
        cause = cause,
    )

    class DependencyUnavailable(
        override val dependency: String,
        override val message: String = "Service temporarily unavailable",
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.DEPENDENCY_UNAVAILABLE,
        httpStatus = HttpStatusCode.ServiceUnavailable.value,
        message = message,
        dependency = dependency,
        cause = cause,
    )

    class UpstreamProtocol(
        override val dependency: String,
        val status: Int,
        override val message: String = "Service temporarily unavailable",
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.DEPENDENCY_UNAVAILABLE,
        httpStatus = HttpStatusCode.BadGateway.value,
        message = message,
        dependency = dependency,
        upstreamStatus = status,
        cause = cause,
    )

    class Internal(
        override val message: String = "Internal server error",
        override val cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.INTERNAL,
        httpStatus = HttpStatusCode.InternalServerError.value,
        message = message,
        cause = cause,
    )
}
