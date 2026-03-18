package dev.kigya.headway.feature.auth.api

import androidx.navigation3.runtime.NavKey
import dev.kigya.headway.navigation.api.route.ScreenRouteHolderContract
import kotlinx.serialization.Serializable

@Serializable
data object AuthScreenKey : NavKey

@Serializable
data object AuthNoAccessScreenKey : NavKey

interface AuthScreenRouteHolderContract : ScreenRouteHolderContract

interface AuthNoAccessScreenRouteHolderContract : ScreenRouteHolderContract
