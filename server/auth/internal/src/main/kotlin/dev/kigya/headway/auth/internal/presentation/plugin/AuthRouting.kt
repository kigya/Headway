package dev.kigya.headway.auth.internal.presentation.plugin

import dev.kigya.headway.auth.api.model.`in`.AuthGoogleLoginPayloadDto
import dev.kigya.headway.auth.api.model.`in`.AuthRefreshTokenPayloadDto
import dev.kigya.headway.auth.api.model.resource.AuthGoogleResource
import dev.kigya.headway.auth.api.model.resource.AuthRefreshTokenResource
import dev.kigya.headway.auth.api.url.authServiceUrlHolder
import dev.kigya.headway.auth.internal.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.auth.internal.domain.usecase.RefreshTokenUseCase
import dev.kigya.headway.common.extension.healthzRouting
import io.ktor.server.application.Application
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.resources.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.authRouting(
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshTokenUseCase,
) {
    routing {
        route(authServiceUrlHolder.baseUrl) {
            healthzRouting()
            authByGoogle(loginWithGoogle = loginWithGoogle)
            refreshToken(refreshToken = refreshToken)
        }
    }
}

private fun Route.authByGoogle(loginWithGoogle: LoginWithGoogleUseCase) {
    post<AuthGoogleResource> {
        val body = call.receive<AuthGoogleLoginPayloadDto>()
        val idToken = body.idToken.trim()
        val fingerprint = body.fingerprint.trim()

        if (idToken.isBlank()) throw BadRequestException("ID token is blank")
        if (fingerprint.isBlank()) throw BadRequestException("Fingerprint is blank")

        call.respond(loginWithGoogle(idToken, fingerprint))
    }
}

private fun Route.refreshToken(refreshToken: RefreshTokenUseCase) {
    post<AuthRefreshTokenResource> {
        val body = call.receive<AuthRefreshTokenPayloadDto>()
        val refreshToken = body.refreshToken.trim()
        val fingerprint = body.fingerprint.trim()

        if (refreshToken.isBlank()) throw BadRequestException("Refresh token is blank")
        if (fingerprint.isBlank()) throw BadRequestException("Fingerprint is blank")

        call.respond(refreshToken(refreshToken, fingerprint))
    }
}
