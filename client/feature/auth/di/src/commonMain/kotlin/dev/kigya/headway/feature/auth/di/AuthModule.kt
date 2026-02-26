package dev.kigya.headway.feature.auth.di

import dev.kigya.headway.feature.auth.api.AuthScreenRouteHolderContract
import dev.kigya.headway.feature.auth.internal.ui.route.AuthScreenRouteHolder
import dev.kigya.headway.feature.auth.internal.ui.screen.AuthStoreFactory
import dev.kigya.headway.feature.auth.internal.ui.screen.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule
    get() = module {
        factoryOf(::AuthStoreFactory)
        viewModelOf(::AuthViewModel)
        singleOf(::AuthScreenRouteHolder) bind AuthScreenRouteHolderContract::class
    }
