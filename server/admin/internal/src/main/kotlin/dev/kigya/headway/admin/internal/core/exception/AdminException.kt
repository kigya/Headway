package dev.kigya.headway.admin.internal.core.exception

import io.ktor.http.HttpStatusCode

internal sealed class AdminException(
    override val message: String,
    override val cause: Throwable? = null,
) : RuntimeException(message, cause) {
    class InvalidRequest(
        message: String,
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    class Unauthorized(
        message: String,
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    open class Forbidden(
        message: String,
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    class NotFound(
        message: String,
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    open class Conflict(
        message: String,
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    class DependencyUnavailable(
        val dependency: String,
        message: String = "Service temporarily unavailable",
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    class UpstreamProtocol(
        val dependency: String,
        val status: Int,
        message: String = "Service temporarily unavailable",
        cause: Throwable? = null,
    ) : AdminException(message = message, cause = cause)

    val httpStatus: HttpStatusCode
        get() = when (this) {
            is InvalidRequest -> HttpStatusCode.BadRequest
            is Unauthorized -> HttpStatusCode.Unauthorized
            is Forbidden -> HttpStatusCode.Forbidden
            is NotFound -> HttpStatusCode.NotFound
            is Conflict -> HttpStatusCode.Conflict
            is DependencyUnavailable -> HttpStatusCode.ServiceUnavailable
            is UpstreamProtocol -> HttpStatusCode.BadGateway
        }
}
