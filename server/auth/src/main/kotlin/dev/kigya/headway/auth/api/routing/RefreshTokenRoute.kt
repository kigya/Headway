package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.error.InvalidRefreshTokenException
import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import ext.respondBadRequest
import ext.respondServerError
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

private const val KEY_REFRESH_TOKEN = "refresh_token"
private const val KEY_FINGERPRINT = "fingerprint"

internal fun Route.refreshToken(auth: AuthUseCaseContract) {
    post("/refreshToken") {
        val refreshToken = call.request.queryParameters[KEY_REFRESH_TOKEN]
        val fingerprint = call.request.queryParameters[KEY_FINGERPRINT]
        if (refreshToken.isNullOrBlank() || fingerprint.isNullOrBlank()) {
            call.respondBadRequest("Query parameter '$KEY_REFRESH_TOKEN' or '$KEY_FINGERPRINT' are missing or empty.")
            return@post
        }

        try {
            call.respond(auth.refreshToken(refreshToken = refreshToken, fingerprint = fingerprint))
        } catch (e: InvalidRefreshTokenException) {
            call.respondBadRequest(e)
        } catch (e: Exception) {
            call.respondServerError(e)
        }
    }
}
