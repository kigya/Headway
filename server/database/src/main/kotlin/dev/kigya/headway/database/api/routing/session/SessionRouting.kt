package dev.kigya.headway.database.api.routing.session

import dev.kigya.headway.database.api.error.BadRequestApiException
import dev.kigya.headway.database.api.port.CreateSessionUseCaseContract
import dev.kigya.headway.database.api.port.ValidateSessionUseCaseContract
import dev.kigya.headway.database.api.routing.session.request.CreateSessionRequestDto
import dev.kigya.headway.database.api.routing.session.request.ValidateSessionRequestDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.sessionRouting(
    createSession: CreateSessionUseCaseContract,
    validateSession: ValidateSessionUseCaseContract,
) {
    post("/session") {
        val request = runCatching { call.receive<CreateSessionRequestDto>() }
            .getOrElse { throw BadRequestApiException() }

        createSession(
            userId = request.userId,
            refreshToken = request.refreshToken.trim(),
            expiresIn = request.expiresIn,
            fingerprint = request.fingerprint.trim(),
        )
        call.respond(HttpStatusCode.Created)
    }

    post("/session/validate") {
        val request = runCatching { call.receive<ValidateSessionRequestDto>() }
            .getOrElse { throw BadRequestApiException() }

        validateSession(
            refreshToken = request.refreshToken.trim(),
            fingerprint = request.fingerprint.trim(),
        )
        call.respond(HttpStatusCode.OK)
    }
}
