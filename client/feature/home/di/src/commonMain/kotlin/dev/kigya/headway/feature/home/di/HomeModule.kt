package dev.kigya.headway.feature.home.di

import dev.kigya.headway.feature.home.api.HomeScreenRouteHolderContract
import dev.kigya.headway.feature.home.internal.ui.route.HomeScreenRouteHolder
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStoreFactory
import dev.kigya.headway.feature.home.internal.ui.screen.HomeViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val homeModule
    get() = module {
        factoryOf(::HomeStoreFactory)
        viewModelOf(::HomeViewModel)
        singleOf(::HomeScreenRouteHolder) bind HomeScreenRouteHolderContract::class
    }
