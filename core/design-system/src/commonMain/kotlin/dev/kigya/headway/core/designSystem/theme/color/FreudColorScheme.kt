package dev.kigya.headway.core.designSystem.theme.color

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.LocalTheme

@Immutable
class FreudColorScheme internal constructor()

@Immutable
data class FreudDynamicColor(
    val light: FreudDsToken<Color>,
    val dark: FreudDsToken<Color>,
)

@Composable
infix fun FreudColorScheme.provides(colors: FreudDynamicColor): FreudDsToken<Color> {
    val isDarkTheme = LocalTheme.current.isDark
    return if (isDarkTheme) colors.dark else colors.light
}
