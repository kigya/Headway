package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

@Immutable
sealed interface FreudTextAnimation {
    val durationMs: Int
    val delayMs: Int
    val startTrigger: FreudAnimationTrigger?

    data class Typewriter(
        override val durationMs: Int = 1000,
        override val delayMs: Int = 0,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation

    data class FadeIn(
        override val durationMs: Int = 500,
        override val delayMs: Int = 0,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation

    data class SlideIn(
        override val durationMs: Int = 600,
        override val delayMs: Int = 0,
        override val startTrigger: FreudAnimationTrigger? = null,
    ) : FreudTextAnimation
}

class FreudAnimationTrigger {
    internal val isFinished: MutableState<Boolean> = mutableStateOf(false)
    fun reset() {
        isFinished.value = false
    }
}
