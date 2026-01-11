package dev.kigya.headway.internal

import dev.kigya.headway.api.installGatewayApi
import dev.kigya.headway.internal.config.ConfigurationValues
import dev.kigya.headway.internal.di.gatewayInternalModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.GATEWAY_SERVICE_PORT,
        host = ConfigurationValues.GATEWAY_SERVICE_HOST,
        module = Application::gatewayApp,
    ).start(wait = true)
}

private fun Application.gatewayApp() {
    install(Koin) {
        modules(gatewayInternalModule)
    }
    installGatewayApi(
        checkHealthStatusUseCaseContract = get(),
        loginWithGoogleUseCaseContract = get(),
        refreshTokenUseCaseContract = get(),
    )
}
