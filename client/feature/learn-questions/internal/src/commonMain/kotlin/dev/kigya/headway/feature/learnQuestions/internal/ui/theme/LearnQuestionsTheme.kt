package dev.kigya.headway.feature.learnQuestions.internal.ui.theme

import androidx.compose.runtime.Composable
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

internal object LearnQuestionsTheme : FreudTheme() {
    val FreudColorScheme.learnBackground
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.learnPrimaryText
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown90,
            dark = super.color.brown40,
        )

    val FreudColorScheme.learnActionContainer
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown40,
        )

    val FreudColorScheme.learnActionContent
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )
}
