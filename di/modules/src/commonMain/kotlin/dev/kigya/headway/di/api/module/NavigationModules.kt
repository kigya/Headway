package dev.kigya.headway.di.api.module

import dev.kigya.headway.navigation.di.headwayNavigationModule
import org.koin.core.module.Module

val navigationModules: List<Module>
    get() = listOf(
        headwayNavigationModule,
    )
