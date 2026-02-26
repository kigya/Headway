package dev.kigya.headway.feature.splash.api

import androidx.navigation3.runtime.NavKey
import dev.kigya.headway.navigation.api.route.ScreenRouteHolderContract
import kotlinx.serialization.Serializable

@Serializable
data object SplashScreenKey : NavKey

interface SplashScreenRouteHolderContract : ScreenRouteHolderContract
