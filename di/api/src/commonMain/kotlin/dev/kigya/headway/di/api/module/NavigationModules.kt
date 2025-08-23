package dev.kigya.headway.di.api.module

import dev.kigya.headway.navigation.internal.headwayNavigationModule
import org.koin.core.module.Module

val navigationModules: List<Module>
    get() = listOf(
        headwayNavigationModule,
    )
