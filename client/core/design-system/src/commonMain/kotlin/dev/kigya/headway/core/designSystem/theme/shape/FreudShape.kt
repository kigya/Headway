package dev.kigya.headway.core.designSystem.theme.shape

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.dimension.FreudDimension

@Immutable
data object FreudShape {
    val circle: FreudDsToken<RoundedCornerShape>
        @Composable get() = FreudDsToken(CircleShape)

    val rounding8: FreudDsToken<RoundedCornerShape>
        @Composable get() = FreudDsToken(RoundedCornerShape(FreudDimension.dp8.value))

    val rounding16: FreudDsToken<RoundedCornerShape>
        @Composable get() = FreudDsToken(RoundedCornerShape(FreudDimension.dp16.value))

    val rounding24: FreudDsToken<RoundedCornerShape>
        @Composable get() = FreudDsToken(RoundedCornerShape(FreudDimension.dp24.value))

    val rounding32: FreudDsToken<RoundedCornerShape>
        @Composable get() = FreudDsToken(RoundedCornerShape(FreudDimension.dp32.value))
}
