package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.auth.api.url.AuthKoinHttpClient
import dev.kigya.headway.auth.api.url.authServiceUrlHolder
import dev.kigya.headway.common.extension.createServiceHttpClient
import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.database.api.url.databaseServiceUrlHolder
import dev.kigya.headway.gateway.core.config.ConfigurationValues.AUTH_SERVICE_HOST
import dev.kigya.headway.gateway.core.config.ConfigurationValues.AUTH_SERVICE_PORT
import dev.kigya.headway.gateway.core.config.ConfigurationValues.DATABASE_SERVICE_HOST
import dev.kigya.headway.gateway.core.config.ConfigurationValues.DATABASE_SERVICE_PORT
import dev.kigya.headway.gateway.core.config.ConfigurationValues.HOME_SERVICE_HOST
import dev.kigya.headway.gateway.core.config.ConfigurationValues.HOME_SERVICE_PORT
import dev.kigya.headway.home.api.url.HomeKoinHttpClient
import dev.kigya.headway.home.api.url.homeServiceUrlHolder
import org.koin.core.module.Module

internal fun Module.httpClients() {
    createServiceHttpClient<DatabaseKoinHttpClient>(
        host = DATABASE_SERVICE_HOST,
        port = DATABASE_SERVICE_PORT,
        baseUrl = databaseServiceUrlHolder.baseUrl,
    )

    createServiceHttpClient<AuthKoinHttpClient>(
        host = AUTH_SERVICE_HOST,
        port = AUTH_SERVICE_PORT,
        baseUrl = authServiceUrlHolder.baseUrl,
    )

    createServiceHttpClient<HomeKoinHttpClient>(
        host = HOME_SERVICE_HOST,
        port = HOME_SERVICE_PORT,
        baseUrl = homeServiceUrlHolder.baseUrl,
    )
}
