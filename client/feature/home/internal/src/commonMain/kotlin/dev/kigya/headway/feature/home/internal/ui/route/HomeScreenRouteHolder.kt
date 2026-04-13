package dev.kigya.headway.feature.home.internal.ui.route

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.home.api.HomeScreenKey
import dev.kigya.headway.feature.home.api.HomeScreenRouteHolderContract
import dev.kigya.headway.feature.home.internal.ui.screen.HomeScreen

class HomeScreenRouteHolder : HomeScreenRouteHolderContract {
    override val screenNavigationKey = HomeScreenKey

    override val content = @Composable { HomeScreen() }
}
