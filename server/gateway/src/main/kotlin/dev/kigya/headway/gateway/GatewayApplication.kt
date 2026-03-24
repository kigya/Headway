package dev.kigya.headway.gateway

import dev.kigya.headway.common.config.CommonConfigurationValues
import dev.kigya.headway.gateway.core.config.ConfigurationValues
import dev.kigya.headway.gateway.di.gatewayDependencies
import dev.kigya.headway.gateway.presentation.GatewayApiBindings
import dev.kigya.headway.gateway.presentation.installGatewayApi
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
        modules(gatewayDependencies)
    }
    installGatewayApi(
        GatewayApiBindings(
            environment = CommonConfigurationValues.environment,
            checkHealthStatus = get(),
            loginWithGoogle = get(),
            loginAsGuest = get(),
            refreshToken = get(),
            inviteUser = get(),
            resolvePrincipal = get(),
            getHomeScreen = get(),
        ),
    )
}
