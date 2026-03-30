package dev.kigya.headway.feature.auth.internal.ui.layout

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal val authSideBySideActionButtonWidth = 348.dp
internal val authSideBySideMinTextColumnWidth = 320.dp

internal fun resolveAuthSideBySideLottieColumnWidth(maxWidth: Dp): Dp =
    minOf(AUTH_SIDE_BY_SIDE_LOTTIE_MAX_WIDTH, maxWidth * AUTH_SIDE_BY_SIDE_LOTTIE_WIDTH_FRACTION)

internal fun shouldUseAuthSideBySideLayout(
    containerWidth: Dp,
    widthSizeClass: WindowWidthSizeClass,
    lottieSideBudget: Dp,
    textColumnHorizontalPadding: Dp,
): Boolean {
    if (widthSizeClass != WindowWidthSizeClass.Expanded) {
        return false
    }
    val minTotal = lottieSideBudget + authSideBySideMinTextColumnWidth + textColumnHorizontalPadding
    return containerWidth >= minTotal
}

private val AUTH_SIDE_BY_SIDE_LOTTIE_MAX_WIDTH = 520.dp
private const val AUTH_SIDE_BY_SIDE_LOTTIE_WIDTH_FRACTION = 0.42f
