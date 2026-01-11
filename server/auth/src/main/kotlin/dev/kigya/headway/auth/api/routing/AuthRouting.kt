package dev.kigya.headway.auth.api.routing

import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import io.ktor.server.routing.Route

internal fun Route.authRouting(auth: AuthUseCaseContract) {
    healhzRoute()
    authByGoogle(auth)
    refreshToken(auth)
}
