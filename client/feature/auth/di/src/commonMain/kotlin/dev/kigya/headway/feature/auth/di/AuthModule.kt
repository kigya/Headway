package dev.kigya.headway.feature.auth.di

import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenRouteHolderContract
import dev.kigya.headway.feature.auth.api.AuthScreenRouteHolderContract
import dev.kigya.headway.feature.auth.internal.ui.route.access.AuthNoAccessScreenRouteHolder
import dev.kigya.headway.feature.auth.internal.ui.route.auth.AuthScreenRouteHolder
import dev.kigya.headway.feature.auth.internal.ui.screen.access.AuthNoAccessStoreFactory
import dev.kigya.headway.feature.auth.internal.ui.screen.access.AuthNoAccessViewModel
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStoreFactory
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authModule
    get() = module {
        factoryOf(::AuthStoreFactory)
        factoryOf(::AuthNoAccessStoreFactory)
        viewModelOf(::AuthViewModel)
        viewModelOf(::AuthNoAccessViewModel)
        singleOf(::AuthScreenRouteHolder) bind AuthScreenRouteHolderContract::class
        singleOf(::AuthNoAccessScreenRouteHolder) bind AuthNoAccessScreenRouteHolderContract::class
    }
