package dev.kigya.headway.database.api

import dev.kigya.headway.database.api.port.RefreshSessionsServiceContract
import dev.kigya.headway.database.api.port.UsersServiceContract
import dev.kigya.headway.database.api.routing.databaseRouting
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json

fun Application.installDatabaseApi(
    usersService: UsersServiceContract,
    refreshSessionsService: RefreshSessionsServiceContract,
    basePath: String = "/internal/v1/database",
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
        route(basePath) {
            databaseRouting(
                usersService = usersService,
                refreshSessionsService = refreshSessionsService,
            )
        }
    }
}
