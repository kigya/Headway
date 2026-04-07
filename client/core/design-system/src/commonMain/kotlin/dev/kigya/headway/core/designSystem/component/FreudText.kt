package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.util.HandleAnimationFinishEffect
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.rememberFreudTextVisualState
import dev.kigya.headway.core.designSystem.util.resolveAnnotatedString

@Composable
fun FreudText(
    value: FreudTextValue,
    color: FreudDsToken<Color>,
    typography: FreudDsToken<TextStyle>,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    val fullText = value.resolveAnnotatedString(defaultContentColor = color)
    val anim = value.animation
    val canStart = anim?.startTrigger?.isFinished?.value ?: true

    val transitionState = remember(value) {
        MutableTransitionState(initialState = false).apply { targetState = canStart }
    }
    transitionState.targetState = canStart

    val transition = rememberTransition(transitionState, label = "FreudTextTransition")

    val progress by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = anim?.durationMs ?: 0,
                delayMillis = anim?.delayMs ?: 0,
                easing = LinearEasing,
            )
        },
        label = "FreudTextProgress",
    ) { triggered -> if (triggered) PROGRESS_VISIBLE else PROGRESS_HIDDEN }

    HandleAnimationFinishEffect(transition, anim, value.onFinishTrigger)

    val (visualText, currentAlpha, currentSlide) = rememberFreudTextVisualState(
        progress = progress,
        fullText = fullText,
        animation = anim,
    )

    Text(
        text = visualText,
        modifier = modifier.graphicsLayer {
            alpha = currentAlpha
            translationY = currentSlide
        },
        color = if (value is FreudTextValue.RichText) Color.Unspecified else color.value,
        style = typography.value,
        textAlign = align,
        maxLines = maxLines,
        minLines = minLines,
        overflow = TextOverflow.Ellipsis,
    )
}

private const val PROGRESS_HIDDEN = 0f
private const val PROGRESS_VISIBLE = 1f
