package dev.kigya.headway.feature.auth.internal.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp

@Composable
internal fun AuthSideBySideLottieColumn(
    lottieColumnWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(width = lottieColumnWidth)
            .clip(shape = RectangleShape),
        contentAlignment = Alignment.BottomStart,
        content = content,
    )
}
