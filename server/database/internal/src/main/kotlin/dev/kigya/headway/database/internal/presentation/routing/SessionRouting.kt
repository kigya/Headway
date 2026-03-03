package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.api.model.`in`.DatabaseCreateSessionPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseValidateSessionPayloadDto
import dev.kigya.headway.database.api.model.resource.DatabaseResource
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

internal fun Route.sessionRouting(
    createSession: CreateSessionUseCase,
    validateSession: ValidateSessionUseCase,
) {
    createSessionRoute(createSession)
    validateSessionRoute(validateSession)
}

private fun Route.createSessionRoute(createSession: CreateSessionUseCase) {
    post<DatabaseResource.Session> {
        val body = call.receive<DatabaseCreateSessionPayloadDto>()

        val refreshToken = body.refreshToken.trim()
        val fingerprint = body.fingerprint.trim()
        if (refreshToken.isBlank()) throw BadRequestException("Refresh token is blank")
        if (fingerprint.isBlank()) throw BadRequestException("Fingerprint is blank")

        createSession(
            userId = body.userId,
            refreshToken = refreshToken,
            expiresIn = body.expiresIn,
            fingerprint = fingerprint,
            platform = body.platform,
        )
        call.respond(HttpStatusCode.Created)
    }
}

private fun Route.validateSessionRoute(validateSession: ValidateSessionUseCase) {
    post<DatabaseResource.Session.Validate> {
        val body = call.receive<DatabaseValidateSessionPayloadDto>()

        val refreshToken = body.refreshToken.trim()
        val fingerprint = body.fingerprint.trim()
        if (refreshToken.isBlank()) throw BadRequestException("Refresh token is blank")
        if (fingerprint.isBlank()) throw BadRequestException("Fingerprint is blank")

        validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )
        call.respond(HttpStatusCode.OK)
    }
}
