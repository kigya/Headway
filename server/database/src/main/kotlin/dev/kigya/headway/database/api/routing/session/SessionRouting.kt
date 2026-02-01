package dev.kigya.headway.database.api.routing.session

import dev.kigya.headway.database.api.error.SessionDoesNotExistsException
import dev.kigya.headway.database.api.error.SessionValidationException
import dev.kigya.headway.database.api.port.RefreshSessionsServiceContract
import dev.kigya.headway.database.api.routing.session.request.CreateSessionRequestDto
import dev.kigya.headway.database.api.routing.session.request.ValidateSessionRequestDto
import dev.kigya.headway.common.extension.respondServerError
import dev.kigya.headway.common.extension.respondUnauthorized
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import dev.kigya.headway.common.model.CommonApiError

internal fun Route.sessionRouting(refreshSessionsService: RefreshSessionsServiceContract) {
    post("/session") {
        val request = call.receive<CreateSessionRequestDto>()

        try {
            refreshSessionsService.createSession(
                userId = request.userId,
                refreshToken = request.refreshToken,
                expiresIn = request.expiresIn,
                fingerprint = request.fingerprint,
            )
            call.respond(HttpStatusCode.Created)
        } catch (e: Exception) {
            call.respond(
                status = HttpStatusCode.InternalServerError,
                message = CommonApiError(
                    message = e.message.toString(),
                    stackTrace = e.stackTrace.map { it.toString() },
                )
            )
        }
    }

    post("/session/validate") {
        val request = call.receive<ValidateSessionRequestDto>()

        try {
            refreshSessionsService.verifySession(
                rawRefreshToken = request.refreshToken,
                fingerprint = request.fingerprint,
            )
            call.respond(HttpStatusCode.OK)
        } catch (e: SessionValidationException) {
            call.respondUnauthorized(e)
        } catch (e: SessionDoesNotExistsException) {
            call.respondUnauthorized(e)
        } catch (e: Exception) {
            call.respondServerError(e)
        }
    }
}
