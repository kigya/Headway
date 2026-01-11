package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.error.GoogleIdTokenValidationException
import dev.kigya.headway.auth.api.error.UserNotActiveException
import dev.kigya.headway.auth.api.model.LoginRequest
import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import ext.respondBadRequest
import ext.respondForbidden
import ext.respondServerError
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

private const val KEY_ID_TOKEN = "id_token"
private const val KEY_FINGERPRINT = "fingerprint"

internal fun Route.authByGoogle(auth: AuthUseCaseContract) {
    post("/google") {
        val body = runCatching { call.receive<LoginRequest>() }
            .getOrElse {
                call.respond(HttpStatusCode.BadRequest, "Invalid request body.")
                return@post
            }

        val idToken = body.idToken.trim()
        val fingerprint = body.fingerprint.trim()

        if (idToken.isBlank() || fingerprint.isBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                "Body field '$KEY_ID_TOKEN' or '$KEY_FINGERPRINT' is missing or empty.",
            )
            return@post
        }

        try {
            call.respond(auth.loginWithGoogle(idToken, fingerprint))
        } catch (e: GoogleIdTokenValidationException) {
            call.application.log.error("Failed to login with Google", e)
            call.respondBadRequest(e)
        } catch (e: UserNotActiveException) {
            call.application.log.warn("User is not active", e)
            call.respondForbidden(e)
        } catch (e: Exception) {
            call.respondServerError(e)
        }
    }
}
