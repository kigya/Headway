package dev.kigya.headway.core.designSystem.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import dev.kigya.headway.core.designSystem.theme.FreudDsToken

/**
 * Extension for [String] to create an [AnnotatedString] by coloring a specific substring.
 * * @param subString The target text to colorize.
 * @param color The [FreudDsToken] color from the design system.
 * @return [AnnotatedString] with the styled substring, or plain text if no match is found.
 */
fun String.colorizeSubStrings(
    subString: String,
    color: FreudDsToken<Color>
): AnnotatedString {
    val fullText = this
    val startIndex = fullText.indexOf(subString)

    if (startIndex == -1) return AnnotatedString(fullText)

    return buildAnnotatedString {
        append(fullText.substring(0, startIndex))
        withStyle(style = SpanStyle(color = color.value)) {
            append(subString)
        }
        append(fullText.substring(startIndex + subString.length))
    }
}