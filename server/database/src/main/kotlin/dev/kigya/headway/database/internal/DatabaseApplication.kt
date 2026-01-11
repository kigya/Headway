package dev.kigya.headway.database.internal

import dev.kigya.headway.database.api.installDatabaseApi
import dev.kigya.headway.database.api.port.RefreshSessionsServiceContract
import dev.kigya.headway.database.api.port.UsersServiceContract
import dev.kigya.headway.database.internal.config.ConfigurationValues
import dev.kigya.headway.database.internal.di.databaseModule
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
    install(Koin) { modules(databaseModule) }
    installDatabaseApi(
        usersService = get<UsersServiceContract>(),
        refreshSessionsService = get<RefreshSessionsServiceContract>(),
    )
}
