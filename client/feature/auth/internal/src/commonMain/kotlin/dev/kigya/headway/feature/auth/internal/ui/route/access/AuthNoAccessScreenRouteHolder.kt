package dev.kigya.headway.feature.auth.internal.ui.route.access

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenKey
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenRouteHolderContract
import dev.kigya.headway.feature.auth.internal.ui.screen.access.NoAccessScreen

class AuthNoAccessScreenRouteHolder : AuthNoAccessScreenRouteHolderContract {
    override val screenNavigationKey = AuthNoAccessScreenKey

    override val content = @Composable { NoAccessScreen() }
}
