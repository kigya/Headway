package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme

@Immutable
enum class FreudSpacerOrientation {
    VERTICAL,
    HORIZONTAL,
}

@Composable
fun ColumnScope.FreudSpacer(
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp> = FreudTheme.DefaultFreudTheme.dimension.dp0,
) {
    drawOrientedSpacer(
        modifier = modifier,
        orientation = FreudSpacerOrientation.VERTICAL,
        size = size,
    )
}

@Composable
fun RowScope.FreudSpacer(
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp> = FreudTheme.DefaultFreudTheme.dimension.dp0,
) {
    drawOrientedSpacer(
        modifier = modifier,
        orientation = FreudSpacerOrientation.HORIZONTAL,
        size = size,
    )
}

@Composable
private fun drawOrientedSpacer(
    orientation: FreudSpacerOrientation,
    size: FreudDsToken<Dp>,
    modifier: Modifier = Modifier,
) = if (orientation == FreudSpacerOrientation.VERTICAL) {
    Spacer(modifier = Modifier.height(size.value).then(modifier))
} else {
    Spacer(modifier = Modifier.width(size.value).then(modifier))
}
