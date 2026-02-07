package dev.kigya.headway.database.internal.presentation.routing

import dev.kigya.headway.database.internal.error.BadRequestApiException
import dev.kigya.headway.database.api.model.`in`.DatabaseCreateSessionPayloadDto
import dev.kigya.headway.database.api.model.`in`.DatabaseValidateSessionPayloadDto
import dev.kigya.headway.database.api.model.resource.DatabaseSessionResource
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.sessionRouting(
    createSession: CreateSessionUseCase,
    validateSession: ValidateSessionUseCase,
) {
    createSessionRoute(createSession)
    validateSessionRoute(validateSession)
}

private fun Route.createSessionRoute(createSession: CreateSessionUseCase) {
    post<DatabaseSessionResource>{
        val request = runCatching { call.receive<DatabaseCreateSessionPayloadDto>() }
            .getOrElse { throw BadRequestApiException() }

        createSession(
            userId = request.userId,
            refreshToken = request.refreshToken.trim(),
            expiresIn = request.expiresIn,
            fingerprint = request.fingerprint.trim(),
        )
        call.respond(HttpStatusCode.Created)
    }
}

private fun Route.validateSessionRoute(validateSession: ValidateSessionUseCase) {
    post<DatabaseSessionResource.Validate> {
        val request = runCatching { call.receive<DatabaseValidateSessionPayloadDto>() }
            .getOrElse { throw BadRequestApiException() }

        validateSession(
            refreshToken = request.refreshToken.trim(),
            fingerprint = request.fingerprint.trim(),
        )
        call.respond(HttpStatusCode.OK)
    }
}
