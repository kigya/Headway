package dev.kigya.headway.gateway.di

import dev.kigya.headway.gateway.di.inner.authDependencies
import dev.kigya.headway.gateway.di.inner.databaseDependencies
import dev.kigya.headway.gateway.di.inner.healthDependencies
import dev.kigya.headway.gateway.di.inner.homeScreenDependencies
import dev.kigya.headway.gateway.di.inner.httpClients
import org.koin.dsl.module

internal val gatewayDependencies = module {
    httpClients()
    healthDependencies()
    authDependencies()
    databaseDependencies()
    homeScreenDependencies()
}
