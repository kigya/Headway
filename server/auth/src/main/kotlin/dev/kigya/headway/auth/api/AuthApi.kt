package dev.kigya.headway.auth.api

import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import dev.kigya.headway.auth.api.routing.authRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.installAuthApi(
    auth: AuthUseCaseContract,
    basePath: String = "/internal/v1/auth",
) {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                encodeDefaults = false
            }
        )
    }

    routing {
        route(basePath) { authRouting(auth) }
    }
}
