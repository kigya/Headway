package dev.kigya.headway.feature.splash.internal.ui.route

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.splash.api.SplashRoute
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashScreen
import dev.kigya.headway.navigation.api.contract.ScreenRouteTypeKey

class SplashScreenRouteHolder : SplashScreenRouteHolderContract {
    override val key: ScreenRouteTypeKey = SplashRoute
    override val content = @Composable { SplashScreen() }
}
