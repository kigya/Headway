package dev.kigya.headway

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.LoadedFont
import headway.app.headwayweb.generated.resources.Res

@OptIn(ExperimentalTextApi::class)
@Composable
internal fun WebEmojiFontFallbackEffect() {
    val fontFamilyResolver = LocalFontFamilyResolver.current
    LaunchedEffect(fontFamilyResolver) {
        val emojiBytes = Res.readBytes(EMBEDDED_NOTO_COLOR_EMOJI_PATH)
        val emojiFallback = FontFamily(
            LoadedFont(
                identity = NOTO_COLOR_EMOJI_IDENTITY,
                getData = { emojiBytes },
                weight = FontWeight.Normal,
                style = FontStyle.Normal,
            ),
        )
        fontFamilyResolver.preload(emojiFallback)
    }
}

private const val EMBEDDED_NOTO_COLOR_EMOJI_PATH = "font/NotoColorEmoji.ttf"
private const val NOTO_COLOR_EMOJI_IDENTITY = "NotoColorEmoji"
