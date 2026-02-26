package dev.kigya.headway.feature.splash.internal.ui.route

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.splash.api.SplashScreenKey
import dev.kigya.headway.feature.splash.api.SplashScreenRouteHolderContract
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashScreen

class SplashScreenRouteHolder : SplashScreenRouteHolderContract {
    override val screenNavigationKey = SplashScreenKey

    override val content = @Composable { SplashScreen() }
}
