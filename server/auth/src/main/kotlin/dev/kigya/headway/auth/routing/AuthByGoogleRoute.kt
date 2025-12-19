package dev.kigya.headway.auth.routing

import dev.kigya.headway.auth.domain.model.GoogleIdTokenValidationException
import dev.kigya.headway.auth.domain.service.GoogleAuthService
import ext.respondBadRequest
import ext.respondServerError
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

private const val KEY_ID_TOKEN = "id_token"
private const val KEY_FINGERPRINT = "fingerprint"

internal fun Route.authByGoogle(googleAuthService: GoogleAuthService) {
    post("/google") {
        val idToken = call.request.queryParameters[KEY_ID_TOKEN]
        val fingerprint = call.request.queryParameters[KEY_FINGERPRINT]
        if (idToken.isNullOrBlank() || fingerprint.isNullOrBlank()) {
            call.respond(HttpStatusCode.BadRequest, "Query parameter '$KEY_ID_TOKEN' or '$KEY_FINGERPRINT' is missing or empty.")
            return@post
        }

        try {
            call.respond(googleAuthService.loginWithGoogle(idToken, fingerprint))
        } catch (e: GoogleIdTokenValidationException) {
            call.application.environment.log.error("Failed to login with Google", e)
            call.respondBadRequest(e)
        } catch (e: Exception) {
            call.respondServerError(e)
        }
    }
}
