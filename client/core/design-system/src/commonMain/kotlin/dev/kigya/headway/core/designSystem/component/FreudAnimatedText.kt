package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.util.FreudTextAnimation
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.resolveAnnotatedString

@Composable
fun FreudAnimatedText(
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
        MutableTransitionState(initialState = false)
    }

    transitionState.targetState = canStart

    val transition = rememberTransition(
        transitionState = transitionState,
        label = "FreudTextTransition",
    )

    val progress by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = anim?.durationMs ?: 0,
                delayMillis = anim?.delayMs ?: 0,
                easing = LinearEasing,
            )
        },
        label = "FreudTextProgress",
    ) { triggered ->
        if (triggered) PROGRESS_VISIBLE else PROGRESS_HIDDEN
    }

    if (transition.currentState == transition.targetState && transition.currentState) {
        SideEffect {
            value.onFinishTrigger?.isFinished?.value = true
        }
    }

    if (anim == null) {
        SideEffect {
            value.onFinishTrigger?.isFinished?.value = true
        }
    }

    val currentAlpha = if (anim == null) ALPHA_FULL else progress
    val currentSlide = if (anim is FreudTextAnimation.SlideIn) {
        (PROGRESS_VISIBLE - progress) * SLIDE_IN_OFFSET
    } else {
        PROGRESS_HIDDEN
    }

    val visualText = remember(progress, fullText) {
        if (anim is FreudTextAnimation.Typewriter && fullText.isNotEmpty()) {
            val length = (fullText.length * progress).toInt().coerceIn(0, fullText.length)
            fullText.subSequence(0, length)
        } else {
            fullText
        }
    }

    FreudText(
        value = FreudTextValue.rich {
            append(value = visualText.toString())
        },
        color = color,
        typography = typography,
        modifier = modifier.graphicsLayer {
            alpha = currentAlpha
            translationY = currentSlide
        },
        align = align,
        maxLines = maxLines,
        minLines = minLines,
    )
}

private const val SLIDE_IN_OFFSET = 16f
private const val PROGRESS_HIDDEN = 0f
private const val PROGRESS_VISIBLE = 1f
private const val ALPHA_FULL = 1f
