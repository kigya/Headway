package dev.kigya.headway.common.extension

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import dev.kigya.headway.common.model.CommonApiError

suspend fun RoutingCall.respondBadRequest(e: Exception) {
    respond(
        status = HttpStatusCode.BadRequest,
        message = CommonApiError(
            message = e.message ?: "Unknown error",
            stackTrace = e.stackTrace.map { it.toString() },
        ),
    )
}

suspend fun RoutingCall.respondBadRequest(message: String) {
    respond(
        status = HttpStatusCode.BadRequest,
        message = CommonApiError(message = message),
    )
}

suspend fun RoutingCall.respondServerError(e: Exception) {
    respond(
        status = HttpStatusCode.InternalServerError,
        message = CommonApiError(
            message = e.message ?: "Unknown error",
            stackTrace = e.stackTrace.map { it.toString() },
        ),
    )
}

suspend fun RoutingCall.respondServerError(message: String) {
    respond(
        status = HttpStatusCode.InternalServerError,
        message = CommonApiError(message = message),
    )
}

suspend fun RoutingCall.respondForbidden(e: Exception) {
    respond(
        status = HttpStatusCode.Forbidden,
        message = CommonApiError(
            message = e.message ?: "Forbidden",
            stackTrace = e.stackTrace.map { it.toString() },
        ),
    )
}

suspend fun RoutingCall.respondForbidden(message: String) {
    respond(
        status = HttpStatusCode.Forbidden,
        message = CommonApiError(
            message = message,
            stackTrace = null,
        ),
    )
}

suspend fun RoutingCall.respondUnauthorized(e: Exception) {
    respond(
        status = HttpStatusCode.Unauthorized,
        message = CommonApiError(
            message = e.message ?: "Unknown",
            stackTrace = e.stackTrace.map { it.toString() },
        ),
    )
}

suspend fun RoutingCall.respondUnauthorized(message: String) {
    respond(
        status = HttpStatusCode.Unauthorized,
        message = CommonApiError(message = message),
    )
}
