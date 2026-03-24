package dev.kigya.headway.gateway.core.exception

internal enum class GatewayErrorCode(val rawName: String) {
    BAD_REQUEST("Bad Request"),
    UNAUTHORIZED("Unauthorized"),
    FORBIDDEN("Forbidden"),
    NOT_FOUND("Not Found"),
    INVITATION_REQUIRED("Invitation Required"),
    CONFLICT("Conflict"),
    DEPENDENCY_UNAVAILABLE("Dependency Unavailable"),
    INTERNAL("Internal Server Error"),
}

internal sealed class GatewayException(
    val code: GatewayErrorCode,
    val category: GatewayErrorCategory,
    val reason: GatewayErrorReason,
    val httpStatus: Int,
    message: String,
    val retryable: Boolean = false,
    open val dependency: String? = null,
    open val upstreamStatus: Int? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    class InvalidRequest(
        reason: GatewayErrorReason,
        message: String,
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.BAD_REQUEST,
        category = GatewayErrorCategory.VALIDATION,
        reason = reason,
        httpStatus = 400,
        message = message,
        cause = cause,
    )

    class Unauthorized(
        reason: GatewayErrorReason,
        message: String,
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.UNAUTHORIZED,
        category = GatewayErrorCategory.AUTHENTICATION,
        reason = reason,
        httpStatus = 401,
        message = message,
        cause = cause,
    )

    class Forbidden(
        reason: GatewayErrorReason,
        message: String,
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.FORBIDDEN,
        category = GatewayErrorCategory.AUTHORIZATION,
        reason = reason,
        httpStatus = 403,
        message = message,
        cause = cause,
    )

    class InvitationRequired(
        message: String = "This account is not invited yet",
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.INVITATION_REQUIRED,
        category = GatewayErrorCategory.AUTHORIZATION,
        reason = GatewayErrorReason.USER_NOT_INVITED,
        httpStatus = 403,
        message = message,
        cause = cause,
    )

    class NotFound(
        message: String,
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.NOT_FOUND,
        category = GatewayErrorCategory.NOT_FOUND,
        reason = GatewayErrorReason.NOT_FOUND,
        httpStatus = 404,
        message = message,
        cause = cause,
    )

    class Conflict(
        message: String,
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.CONFLICT,
        category = GatewayErrorCategory.CONFLICT,
        reason = GatewayErrorReason.IDENTITY_CONFLICT,
        httpStatus = 409,
        message = message,
        cause = cause,
    )

    class DependencyUnavailable(
        override val dependency: String,
        message: String = "Service temporarily unavailable",
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.DEPENDENCY_UNAVAILABLE,
        category = GatewayErrorCategory.DEPENDENCY,
        reason = GatewayErrorReason.DEPENDENCY_FAILURE,
        httpStatus = 503,
        message = message,
        retryable = true,
        dependency = dependency,
        cause = cause,
    )

    class UpstreamProtocol(
        override val dependency: String,
        val status: Int,
        message: String = "Service temporarily unavailable",
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.DEPENDENCY_UNAVAILABLE,
        category = GatewayErrorCategory.DEPENDENCY,
        reason = GatewayErrorReason.UPSTREAM_PROTOCOL,
        httpStatus = 502,
        message = message,
        retryable = true,
        dependency = dependency,
        upstreamStatus = status,
        cause = cause,
    )

    class Internal(
        message: String = "Internal server error",
        cause: Throwable? = null,
    ) : GatewayException(
        code = GatewayErrorCode.INTERNAL,
        category = GatewayErrorCategory.INTERNAL,
        reason = GatewayErrorReason.INTERNAL_ERROR,
        httpStatus = 500,
        message = message,
        cause = cause,
    )
}
