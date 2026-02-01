package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.error.BadRequestApiException
import dev.kigya.headway.auth.api.model.RefreshTokenRequest
import dev.kigya.headway.auth.api.port.RefreshTokenUseCaseContract
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.refreshToken(refreshTokenUseCase: RefreshTokenUseCaseContract) {
    post("/refreshToken") {
        val body = runCatching { call.receive<RefreshTokenRequest>() }.getOrElse { throw BadRequestApiException() }

        val token = body.refreshToken.trim()
        val fp = body.fingerprint.trim()
        if (token.isBlank() || fp.isBlank()) throw BadRequestApiException()

        call.respond(refreshTokenUseCase(token, fp))
    }
}

