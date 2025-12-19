package dev.kigya.headway.auth.routing

import dev.kigya.headway.auth.data.database.validateSession
import dev.kigya.headway.auth.domain.model.RefreshTokenResponse
import dev.kigya.headway.auth.domain.service.JWTService
import ext.respondBadRequest
import io.ktor.client.HttpClient
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_FINGERPRINT = "fingerprint"

internal fun Route.refreshToken(
    jwtService: JWTService,
    client: HttpClient,
) {
    post("/refreshToken") {
        val refreshToken = call.request.queryParameters[KEY_REFRESH_TOKEN]
        val fingerprint = call.request.queryParameters[KEY_FINGERPRINT]
        if (refreshToken.isNullOrBlank() || fingerprint.isNullOrBlank()) {
            call.respondBadRequest("Query parameter '$KEY_REFRESH_TOKEN' or '$KEY_FINGERPRINT' are missing or empty.")
            return@post
        }

        if (jwtService.verifyRefreshToken(refreshToken).not()) {
            call.respondBadRequest("Invalid refresh token.")
            return@post
        }

        val validateSessionResponse = client.validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )
        if (validateSessionResponse == false) {
            call.respondBadRequest("Failed to validate session.")
            return@post
        }

        val userUUID = jwtService.getUserUUID(refreshToken)
        if (userUUID == null) {
            call.respondBadRequest("Invalid refresh token. User UUID is null")
            return@post
        }

        val accessToken = jwtService.generateAccessToken(userUUID)
        call.respond(RefreshTokenResponse(accessToken))
    }
}
