package dev.kigya.headway.feature.splash.internal.di

import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.feature.splash.internal.ui.route.SplashScreenRouteHolder
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashStoreFactory
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

context(_: KoinApplication)
val splashModule
    get() = module {
        factoryOf(::SplashStoreFactory)
        viewModelOf(::SplashViewModel)
        singleOf(::SplashScreenRouteHolder) bind SplashScreenRouteHolderContract::class
    }
