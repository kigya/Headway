package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import dev.kigya.headway.core.designSystem.component.FreudLoadingOverlayTheme.background
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.background
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.freud_loading_content_description
import headway.core.design_system.generated.resources.ic_loader
import org.jetbrains.compose.resources.stringResource

private object FreudLoadingOverlayDefaults {
    val loaderSize = FreudTheme.DefaultFreudTheme.dimension.dp72

    const val TRANSITION_DURATION_MILLIS = 350
    const val SLIDE_OFFSET_DIVISOR = 8
    const val LOADER_ROTATION_DURATION_MILLIS = 1_200
}

object FreudLoadingOverlayTestTags {
    const val LOADER_ROOT = "freud_loading_overlay_loader_root"
    const val CONTENT_SLOT = "freud_loading_overlay_content_slot"
}

@Composable
fun FreudLoadingOverlay(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    AnimatedContent(
        targetState = isLoading,
        transitionSpec = { freudLoadingOverlayContentTransform() },
        contentKey = { it },
        modifier = modifier,
        label = "FreudLoadingOverlay",
    ) { isLoadingTarget ->
        if (isLoadingTarget) {
            FreudLoadingIndicatorScreen()
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(FreudLoadingOverlayTestTags.CONTENT_SLOT),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun FreudLoadingIndicatorScreen() {
    val backgroundColor = FreudLoadingOverlayTheme.colorScheme.background
    val loadingContentDescription = stringResource(Res.string.freud_loading_content_description)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = backgroundColor,
                pattern = FreudBackgroundPattern.Waves,
            )
            .testTag(FreudLoadingOverlayTestTags.LOADER_ROOT)
            .semantics { contentDescription = loadingContentDescription },
        contentAlignment = Alignment.Center,
    ) {
        FreudLoadingIndicatorIcon()
    }
}

@Composable
private fun FreudLoadingIndicatorIcon() {
    val infiniteTransition = rememberInfiniteTransition(label = "FreudLoadingIndicator")
    val rotationDegrees by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = FULL_ROTATION_DEGREES,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = FreudLoadingOverlayDefaults.LOADER_ROTATION_DURATION_MILLIS,
                easing = LinearEasing,
            ),
            repeatMode = RepeatMode.Restart,
        ),
        label = "FreudLoadingIndicatorRotation",
    )

    FreudIcon(
        resource = Res.drawable.ic_loader,
        contentDescription = null,
        size = FreudLoadingOverlayDefaults.loaderSize,
        modifier = Modifier.graphicsLayer { rotationZ = rotationDegrees },
    )
}

private object FreudLoadingOverlayTheme : FreudTheme() {
    val FreudColorScheme.background: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )
}

private typealias FreudLoadingOverlayTransitionScope =
    AnimatedContentTransitionScope<Boolean>

private fun FreudLoadingOverlayTransitionScope.freudLoadingOverlayContentTransform(): ContentTransform {
    val durationMillis = FreudLoadingOverlayDefaults.TRANSITION_DURATION_MILLIS
    val offsetDivisor = FreudLoadingOverlayDefaults.SLIDE_OFFSET_DIVISOR
    val enter = fadeIn(tween(durationMillis)) +
        slideInVertically(tween(durationMillis)) { it / offsetDivisor }
    val exit = fadeOut(tween(durationMillis)) +
        slideOutVertically(tween(durationMillis)) { -it / offsetDivisor }
    return enter togetherWith exit
}

private const val FULL_ROTATION_DEGREES = 360f
