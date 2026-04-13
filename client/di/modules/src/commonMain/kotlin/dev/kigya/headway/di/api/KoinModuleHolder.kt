package dev.kigya.headway.di.api

import dev.kigya.headway.core.session.di.sessionDomainModule
import dev.kigya.headway.di.api.module.dispatcherModule
import dev.kigya.headway.di.api.module.featureModules
import dev.kigya.headway.di.api.module.navigationModules
import dev.kigya.headway.di.api.module.sessionInfrastructureModule
import dev.kigya.headway.di.api.module.sessionPlatformModule
import org.koin.core.module.Module

val appModules: List<Module>
    get() = listOf(
        sessionPlatformModule(),
        sessionInfrastructureModule(),
        sessionDomainModule(),
    ) + featureModules +
        navigationModules +
        dispatcherModule
