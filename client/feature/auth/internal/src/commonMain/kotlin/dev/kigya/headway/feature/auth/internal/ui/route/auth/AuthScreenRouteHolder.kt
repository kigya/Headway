package dev.kigya.headway.feature.auth.internal.ui.route.auth

import androidx.compose.runtime.Composable
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.auth.api.AuthScreenRouteHolderContract
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthScreen

class AuthScreenRouteHolder : AuthScreenRouteHolderContract {
    override val screenNavigationKey = AuthScreenKey

    override val content = @Composable { AuthScreen() }
}
