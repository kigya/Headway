package dev.kigya.headway.feature.home.api

import androidx.navigation3.runtime.NavKey
import dev.kigya.headway.navigation.api.route.ScreenRouteHolderContract
import kotlinx.serialization.Serializable

@Serializable
data object HomeScreenKey : NavKey

interface HomeScreenRouteHolderContract : ScreenRouteHolderContract
