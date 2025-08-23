package dev.kigya.headway.navigation.internal

import dev.kigya.headway.navigation.api.contract.NavigatorContract
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val headwayNavigationModule
    get() = module {
        singleOf(::HeadwayNavigator) bind NavigatorContract::class
    }
