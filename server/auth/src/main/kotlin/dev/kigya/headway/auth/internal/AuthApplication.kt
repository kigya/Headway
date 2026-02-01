package dev.kigya.headway.auth.internal

import dev.kigya.headway.auth.api.installAuthApi
import dev.kigya.headway.auth.internal.config.ConfigurationValues
import dev.kigya.headway.auth.internal.di.authInternalModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.AUTH_SERVICE_PORT,
        host = ConfigurationValues.AUTH_SERVICE_HOST,
        module = Application::authApp,
    ).start(wait = true)
}

internal fun Application.authApp() {
    ConfigurationValues.validateSecrets()

    install(Koin) {
        modules(authInternalModule)
    }
    installAuthApi(
        loginWithGoogleUseCase = get(),
        refreshTokenUseCase = get(),
    )
}
