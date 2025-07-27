package dev.kigya.headway.feature.splash.internal

import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val splashModule = module {
    singleOf(::SplashScreenRouteHolder) bind SplashScreenRouteHolderContract::class
}
