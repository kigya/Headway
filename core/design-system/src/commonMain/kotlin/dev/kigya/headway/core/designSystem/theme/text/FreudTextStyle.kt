package dev.kigya.headway.core.designSystem.theme.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@Immutable
data class FreudTextStyle(
    val fontSize: FreudDsToken<TextUnit>,
    val fontWeight: FreudDsToken<FontWeight>,
    val fontFamily: FreudDsToken<FontFamily>,
    val lineHeight: FreudDsToken<TextUnit> = FreudDsToken(TextUnit.Unspecified),
)

@Composable
infix fun FreudTypography.provides(
    style: FreudTextStyle,
): FreudDsToken<TextStyle> = FreudDsToken(
    TextStyle(
        fontSize = style.fontSize.value,
        fontWeight = style.fontWeight.value,
        fontFamily = style.fontFamily.value,
        lineHeight = style.lineHeight.value,
    )
)
