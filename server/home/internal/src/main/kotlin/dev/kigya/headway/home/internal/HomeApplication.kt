package dev.kigya.headway.home.internal

import dev.kigya.headway.home.internal.core.config.ConfigurationValues
import dev.kigya.headway.home.internal.di.homeInternalModule
import dev.kigya.headway.home.internal.presentation.installHomeApi
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.HOME_SERVICE_PORT,
        host = ConfigurationValues.HOME_SERVICE_HOST,
        module = Application::homeApp,
    ).start(wait = true)
}

internal fun Application.homeApp() {
    ConfigurationValues.validateSecrets()

    install(Koin) {
        modules(homeInternalModule)
    }
    installHomeApi(
        getHomeScreen = get(),
    )
}
