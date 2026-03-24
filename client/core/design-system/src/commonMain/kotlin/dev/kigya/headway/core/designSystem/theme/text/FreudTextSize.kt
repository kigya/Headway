package dev.kigya.headway.core.designSystem.theme.text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@Immutable
data object FreudTextSize {

    val sp180: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(180.sp.nonScaled)
    val sp128: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(128.sp.nonScaled)
    val sp96: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(96.sp.nonScaled)
    val sp72: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(72.sp.nonScaled)
    val sp60: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(60.sp.nonScaled)
    val sp48: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(48.sp.nonScaled)
    val sp38: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(38.sp.nonScaled)
    val sp36: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(36.sp.nonScaled)
    val sp32: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(32.sp.nonScaled)
    val sp30: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(30.sp.nonScaled)
    val sp28: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(28.sp.nonScaled)
    val sp26: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(26.sp.nonScaled)
    val sp24: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(24.sp.nonScaled)
    val sp22: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(22.sp.nonScaled)
    val sp20: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(20.sp.nonScaled)
    val sp18: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(18.sp.nonScaled)
    val sp16: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(16.sp.nonScaled)
    val sp14: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(14.sp.nonScaled)
    val sp12: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(12.sp.nonScaled)
    val sp10: FreudDsToken<TextUnit> @Composable get() = FreudDsToken(10.sp.nonScaled)

    private val TextUnit.nonScaled: TextUnit
        @Composable get() = (value / LocalDensity.current.fontScale).sp
}
