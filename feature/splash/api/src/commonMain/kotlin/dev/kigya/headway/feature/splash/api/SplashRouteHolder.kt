package dev.kigya.headway.feature.splash.api

import dev.kigya.headway.navigation.api.contract.ScreenRouteHolderContract
import dev.kigya.headway.navigation.api.contract.ScreenRouteTypeKey
import kotlinx.serialization.Serializable

@Serializable
data object SplashRoute : ScreenRouteTypeKey

interface SplashScreenRouteHolderContract : ScreenRouteHolderContract
