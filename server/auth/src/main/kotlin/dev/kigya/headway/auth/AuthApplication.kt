package dev.kigya.headway.auth

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

internal fun main() {
    embeddedServer(
        Netty,
        port = ConfigurationValues.AUTH_SERVICE_PORT,
        host = ConfigurationValues.AUTH_SERVICE_HOST,
        module = Application::authService,
    ).start(wait = true)
}
