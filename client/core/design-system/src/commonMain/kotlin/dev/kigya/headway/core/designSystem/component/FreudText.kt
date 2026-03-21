package dev.kigya.headway.core.designSystem.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.util.FreudRichTextContent
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.resolveTextSource

@Composable
fun FreudText(
    value: FreudTextValue,
    color: FreudDsToken<Color>,
    typography: FreudDsToken<TextStyle>,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Center,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
) {
    Text(
        text = value.resolveAnnotatedString(),
        modifier = modifier,
        color = color.value,
        style = typography.value,
        maxLines = maxLines,
        minLines = minLines,
        textAlign = align,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun FreudTextValue.resolveAnnotatedString(): AnnotatedString = when (this) {
    is FreudTextValue.PlainText -> AnnotatedString(
        text = resolveTextSource(source = source),
    )

    is FreudTextValue.RichText -> when (val c = content) {
        is FreudRichTextContent.Segments -> buildAnnotatedString {
            c.value.forEach { segment ->
                val resolvedValue = resolveTextSource(source = segment.source)

                if (segment.color == null) {
                    append(resolvedValue)
                } else {
                    withStyle(
                        style = SpanStyle(color = segment.color.value),
                    ) {
                        append(resolvedValue)
                    }
                }
            }
        }
    }
}
