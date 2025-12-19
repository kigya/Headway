package ext

import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.RoutingCall
import model.CommonApiError

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
