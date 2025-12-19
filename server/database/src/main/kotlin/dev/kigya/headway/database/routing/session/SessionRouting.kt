package dev.kigya.headway.database.routing.session

import dev.kigya.headway.database.domain.model.SessionDoesNotExistsException
import dev.kigya.headway.database.domain.model.SessionValidationException
import dev.kigya.headway.database.domain.service.RefreshSessionsService
import dev.kigya.headway.database.routing.session.request.CreateSessionRequestDto
import dev.kigya.headway.database.routing.session.request.ValidateSessionRequestDto
import ext.respondServerError
import ext.respondUnauthorized
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import model.CommonApiError

internal fun Route.sessionRouting(refreshSessionsService: RefreshSessionsService) {
    post("/session") {
        val request = call.receive<CreateSessionRequestDto>()

        try {
            refreshSessionsService.createSession(
                userId = request.userId,
                refreshToken = request.refreshToken,
                expiresIn = request.expiresIn,
                fingerprint = request.fingerprint,
            )
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
