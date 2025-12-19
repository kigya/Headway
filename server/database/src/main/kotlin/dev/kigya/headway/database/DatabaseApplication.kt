package dev.kigya.headway.database

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.DATABASE_SERVICE_PORT,
        host = ConfigurationValues.DATABASE_SERVICE_HOST,
        module = Application::databaseService,
    ).start(wait = true)
}
