package dev.kigya.headway.core.designSystem.util

import androidx.compose.ui.tooling.preview.datasource.LoremIpsum

internal object PreviewText {
    fun lorem(words: Int): String {
        val safeWords = words.coerceAtLeast(1)
        return LoremIpsum(safeWords).values.first()
    }

    fun loremForMaxLines(
        maxLines: Int,
        oneLineWords: Int = 40,
        multiLineWords: Int = 18,
    ): String = lorem(
        words = if (maxLines == 1) oneLineWords else multiLineWords,
    )
}
