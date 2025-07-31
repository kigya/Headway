package dev.kigya.headway.core.designSystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

@Composable
fun FreudText(
    value: String,
    color: FreudDsToken<Color>,
    typography: FreudDsToken<TextStyle>,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) = Text(
    text = value,
    modifier = modifier,
    color = color.value,
    style = typography.value,
    maxLines = maxLines,
    minLines = minLines,
    textAlign = align,
    overflow = TextOverflow.Ellipsis,
)
