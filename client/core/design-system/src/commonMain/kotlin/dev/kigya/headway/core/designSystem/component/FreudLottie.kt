package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import dev.kigya.headway.core.designSystem.theme.LocalTheme
import io.github.alexzhirkevich.compottie.DotLottie
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

enum class FreudLottieSource {
    Json,
    DotLottie,
}

@Composable
fun FreudLottie(
    reader: suspend () -> ByteArray,
    source: FreudLottieSource,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    isRestartable: Boolean = false,
    shouldBeReversedOnRepeat: Boolean = false,
    speed: Float = 1f,
    iterations: Int = 1,
    contentScale: ContentScale = ContentScale.FillBounds,
    onFinish: () -> Unit = {},
) {
    val currentReader by rememberUpdatedState(reader)
    val currentOnFinish by rememberUpdatedState(onFinish)
    val isDarkTheme = LocalTheme.current.isDark
    val themeId = if (isDarkTheme) DARK_THEME_ID else LIGHT_THEME_ID

    val compositionSpec by produceState<LottieCompositionSpec?>(
        initialValue = null,
        key1 = source,
        key2 = currentReader,
    ) {
        val bytes = currentReader()
        value = when (source) {
            FreudLottieSource.Json ->
                LottieCompositionSpec.JsonString(bytes.decodeToString())

            FreudLottieSource.DotLottie ->
                LottieCompositionSpec.DotLottie(
                    archive = bytes,
                    animationId = null,
                )
        }
    }

    val resolvedCompositionSpec = compositionSpec
    if (resolvedCompositionSpec == null) {
        Spacer(modifier = modifier)
        return
    }

    val composition by rememberLottieComposition { resolvedCompositionSpec }
    if (composition == null) {
        Spacer(modifier = modifier)
        return
    }

    val progress by animateLottieCompositionAsState(
        composition = composition,
        restartOnPlay = isRestartable,
        reverseOnRepeat = shouldBeReversedOnRepeat,
        speed = speed,
        iterations = iterations,
    )

    val isAnimationComplete by remember(progress, iterations) {
        derivedStateOf {
            iterations == 1 && progress >= COMPLETION_THRESHOLD
        }
    }

    LaunchedEffect(isAnimationComplete) {
        if (isAnimationComplete) currentOnFinish()
    }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress },
            theme = when (source) {
                FreudLottieSource.Json -> null
                FreudLottieSource.DotLottie -> themeId
            },
        ),
        contentDescription = null,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
    )
}

private const val LIGHT_THEME_ID = "light"
private const val DARK_THEME_ID = "dark"
private const val COMPLETION_THRESHOLD = 0.999f
