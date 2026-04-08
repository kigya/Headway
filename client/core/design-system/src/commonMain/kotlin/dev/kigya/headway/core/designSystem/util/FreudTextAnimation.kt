package dev.kigya.headway.core.designSystem.util

import androidx.compose.animation.core.Transition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString

@Immutable
sealed interface FreudTextAnimation {
    val durationMs: Int
    val delayMs: Int
    val startTrigger: FreudAnimationTrigger?

    data class Typewriter(
        override val durationMs: Int = DEFAULT_TYPEWRITER_DURATION,
        override val delayMs: Int = DEFAULT_ANIM_DELAY,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation

    data class FadeIn(
        override val durationMs: Int = DEFAULT_FADE_IN_DURATION,
        override val delayMs: Int = DEFAULT_ANIM_DELAY,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation

    data class SlideIn(
        override val durationMs: Int = DEFAULT_SLIDE_IN_DURATION,
        override val delayMs: Int = DEFAULT_ANIM_DELAY,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation
}

class FreudAnimationTrigger {
    internal val isFinished: MutableState<Boolean> = mutableStateOf(false)
    fun reset() {
        isFinished.value = false
    }
}

@Composable
internal fun rememberFreudTextVisualState(
    progress: Float,
    fullText: AnnotatedString,
    animation: FreudTextAnimation?,
): Triple<AnnotatedString, Float, Float> = remember(progress, fullText, animation) {
    val text = if (animation is FreudTextAnimation.Typewriter && fullText.isNotEmpty()) {
        val length = (fullText.length * progress).toInt().coerceIn(0, fullText.length)
        fullText.subSequence(0, length)
    } else {
        fullText
    }

    val alpha = if (animation == null) ALPHA_FULL else progress
    val slide = if (animation is FreudTextAnimation.SlideIn) {
        (PROGRESS_VISIBLE - progress) * SLIDE_IN_OFFSET
    } else {
        0f
    }

    Triple(text, alpha, slide)
}

@Composable
internal fun HandleAnimationFinishEffect(
    transition: Transition<Boolean>,
    animation: FreudTextAnimation?,
    onFinishTrigger: FreudAnimationTrigger?,
) {
    val isEffectivelyFinished = animation == null ||
        (transition.currentState == transition.targetState && transition.currentState)

    if (isEffectivelyFinished) {
        SideEffect {
            onFinishTrigger?.isFinished?.value = true
        }
    }
}

private const val DEFAULT_TYPEWRITER_DURATION = 1000
private const val DEFAULT_FADE_IN_DURATION = 500
private const val DEFAULT_SLIDE_IN_DURATION = 600
private const val DEFAULT_ANIM_DELAY = 0
private const val SLIDE_IN_OFFSET = 16f
private const val PROGRESS_VISIBLE = 1f
private const val ALPHA_FULL = 1f
