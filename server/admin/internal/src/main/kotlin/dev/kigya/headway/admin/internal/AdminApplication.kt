package dev.kigya.headway.admin.internal

import dev.kigya.headway.admin.internal.core.config.ConfigurationValues
import dev.kigya.headway.admin.internal.di.adminInternalModule
import dev.kigya.headway.admin.internal.presentation.installAdminApi
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.ADMIN_SERVICE_PORT,
        host = ConfigurationValues.ADMIN_SERVICE_HOST,
        module = Application::adminApp,
    ).start(wait = true)
}

internal fun Application.adminApp() {
    ConfigurationValues.validateSecrets()

    install(Koin) {
        modules(adminInternalModule)
    }
    installAdminApi(
        config = get(),
        githubOAuthClient = get(),
        inviteUser = get(),
    )
}
