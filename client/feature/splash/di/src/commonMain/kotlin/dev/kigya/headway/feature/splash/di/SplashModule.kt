package dev.kigya.headway.feature.splash.di

import dev.kigya.headway.feature.splash.api.SplashScreenKey
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.feature.splash.internal.ui.route.SplashScreenRouteHolder
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashStoreFactory
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashViewModel
import dev.kigya.headway.navigation.api.navigator.StartKeyProvider
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule
    get() = module {
        single<StartKeyProvider> { StartKeyProvider { SplashScreenKey } }
        factoryOf(::SplashStoreFactory)
        viewModelOf(::SplashViewModel)
        singleOf(::SplashScreenRouteHolder) bind SplashScreenRouteHolderContract::class
    }
