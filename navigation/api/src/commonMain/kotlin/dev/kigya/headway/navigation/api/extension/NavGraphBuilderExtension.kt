package dev.kigya.headway.navigation.api.extension

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import dev.kigya.headway.navigation.api.contract.ScreenRouteHolderContract
import kotlin.reflect.KType

inline fun <reified T : ScreenRouteHolderContract> NavGraphBuilder.animatedComposable(
    route: T,
    typeMap: Map<KType, NavType<*>> = emptyMap(),
    crossinline screen: @Composable ScreenRouteHolderContract.() -> Unit,
) {
    composable(
        route = route.key::class,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() },
        typeMap = typeMap,
    ) { route.screen() }
}
