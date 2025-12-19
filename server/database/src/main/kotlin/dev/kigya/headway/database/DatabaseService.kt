package dev.kigya.headway.database

import dev.kigya.headway.database.di.databaseModule
import dev.kigya.headway.database.routing.databaseRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun Application.databaseService() {
    install(Koin) {
        modules(databaseModule)
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
        route("/internal/v1/database") {
            databaseRouting(get(), get())
        }
    }
}
