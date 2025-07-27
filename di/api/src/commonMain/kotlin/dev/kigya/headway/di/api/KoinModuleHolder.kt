package dev.kigya.headway.di.api

import dev.kigya.headway.di.api.module.featureModules
import dev.kigya.headway.di.api.module.navigationModules
import org.koin.core.module.Module

val appModules: List<Module> =
    featureModules + navigationModules
