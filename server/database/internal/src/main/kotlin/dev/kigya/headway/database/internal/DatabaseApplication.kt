package dev.kigya.headway.database.internal

import dev.kigya.headway.database.internal.core.config.ConfigurationValues
import dev.kigya.headway.database.internal.di.databaseModule
import dev.kigya.headway.database.internal.presentation.DatabaseApplicationUseCases
import dev.kigya.headway.database.internal.presentation.installDatabaseApi
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

internal fun main() {
    embeddedServer(
        factory = Netty,
        port = ConfigurationValues.DATABASE_SERVICE_PORT,
        host = ConfigurationValues.DATABASE_SERVICE_HOST,
        module = Application::databaseApp,
    ).start(wait = true)
}

private fun Application.databaseApp() {
    ConfigurationValues.validateSecrets()

    install(Koin) {
        modules(databaseModule)
    }
    installDatabaseApi(
        useCases = DatabaseApplicationUseCases(
            getUser = get(),
            createGoogleUser = get(),
            upsertGoogleUser = get(),
            inviteUser = get(),
            createSession = get(),
            validateSession = get(),
            preparation = get(),
        ),
    )
}
