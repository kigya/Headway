package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter

@Composable
fun FreudLottie(
    reader: suspend () -> ByteArray,
    modifier: Modifier = Modifier,
    isRestartable: Boolean = false,
    shouldBeReversedOnRepeat: Boolean = false,
    speed: Float = 1f,
    iterations: Int = 1,
    contentScale: ContentScale = ContentScale.FillBounds,
    onFinish: () -> Unit = {},
) {
    val currentReader by rememberUpdatedState(reader)
    val currentOnFinish by rememberUpdatedState(onFinish)
    val json by produceState<String?>(initialValue = null) {
        value = currentReader().decodeToString()
    }
    val composition by rememberLottieComposition(json) { LottieCompositionSpec.JsonString(json.orEmpty()) }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        restartOnPlay = isRestartable,
        reverseOnRepeat = shouldBeReversedOnRepeat,
        speed = speed,
        iterations = iterations,
    )
    val isAnimationComplete by remember(progress) {
        derivedStateOf { progress == 1f }
    }

    LaunchedEffect(isAnimationComplete) { if (isAnimationComplete) currentOnFinish() }

    Image(
        painter = rememberLottiePainter(
            composition = composition,
            progress = { progress },
        ),
        contentDescription = null,
        modifier = modifier,
        contentScale = contentScale,
    )
}
