package dev.kigya.headway.navigation.di

import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.internal.HeadwayNavigator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val headwayNavigationModule
    get() = module {
        singleOf(::HeadwayNavigator) bind NavigatorContract::class
    }
