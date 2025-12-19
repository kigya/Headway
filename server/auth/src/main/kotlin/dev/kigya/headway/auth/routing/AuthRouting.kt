package dev.kigya.headway.auth.routing

import dev.kigya.headway.auth.domain.service.GoogleAuthService
import dev.kigya.headway.auth.domain.service.JWTService
import io.ktor.client.HttpClient
import io.ktor.server.routing.Route

internal fun Route.authRouting(
    googleAuthService: GoogleAuthService,
    jwtService: JWTService,
    client: HttpClient,
) {
    authByGoogle(googleAuthService)
    refreshToken(jwtService, client)
}
