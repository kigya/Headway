package dev.kigya.headway.auth

import dev.kigya.headway.auth.di.authModule
import dev.kigya.headway.auth.routing.authRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun Application.authService() {
    install(Koin) {
        modules(authModule)
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                encodeDefaults = false
            }
        )
    }
    routing {
        route("/internal/v1/auth") {
            authRouting(
                googleAuthService = get(),
                jwtService = get(),
                client = get(),
            )
        }
    }
}
