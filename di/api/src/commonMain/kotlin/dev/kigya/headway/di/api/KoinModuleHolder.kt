package dev.kigya.headway.di.api

import dev.kigya.headway.di.api.module.featureModules
import dev.kigya.headway.di.api.module.navigationModules
import org.koin.core.KoinApplication
import org.koin.core.module.Module

context(_: KoinApplication)
val appModules: List<Module>
    get() = featureModules + navigationModules
