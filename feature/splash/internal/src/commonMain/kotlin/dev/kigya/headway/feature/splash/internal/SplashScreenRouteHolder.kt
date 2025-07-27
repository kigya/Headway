package dev.kigya.headway.feature.splash.internal

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.splash.api.SplashRoute
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.navigation.api.contract.ScreenRouteTypeKey

class SplashScreenRouteHolder : SplashScreenRouteHolderContract {
    override val screenRouteTypeKey: ScreenRouteTypeKey = SplashRoute
    override val content = @Composable { SplashScreen() }
}
