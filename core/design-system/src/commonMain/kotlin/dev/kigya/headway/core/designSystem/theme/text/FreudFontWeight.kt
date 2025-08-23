package dev.kigya.headway.core.designSystem.theme.text

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.font.FontWeight
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@Immutable
data object FreudFontWeight {
    val extraBold: FreudDsToken<FontWeight> = FreudDsToken(FontWeight.ExtraBold)
    val bold: FreudDsToken<FontWeight> = FreudDsToken(FontWeight.Bold)
    val semiBold: FreudDsToken<FontWeight> = FreudDsToken(FontWeight.SemiBold)
}
