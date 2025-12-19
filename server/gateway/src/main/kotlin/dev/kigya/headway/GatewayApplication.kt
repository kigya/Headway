package dev.kigya.headway

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.GATEWAY_SERVICE_PORT,
        host = ConfigurationValues.GATEWAY_SERVICE_HOST,
        module = Application::publicGatewayService,
    ).start(wait = true)
}
