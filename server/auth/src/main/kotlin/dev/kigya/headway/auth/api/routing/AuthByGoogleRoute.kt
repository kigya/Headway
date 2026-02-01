package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.error.BadRequestApiException
import dev.kigya.headway.auth.api.model.LoginWithGoogleRequest
import dev.kigya.headway.auth.api.port.LoginWithGoogleUseCaseContract
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

internal fun Route.authByGoogle(loginWithGoogleUseCase: LoginWithGoogleUseCaseContract) {
    post("/google") {
        val body = runCatching { call.receive<LoginWithGoogleRequest>() }.getOrElse { throw BadRequestApiException() }

        val idToken = body.idToken.trim()
        val fingerprint = body.fingerprint.trim()
        if (idToken.isBlank() || fingerprint.isBlank()) throw BadRequestApiException()

        call.respond(loginWithGoogleUseCase(idToken, fingerprint))
    }
}
