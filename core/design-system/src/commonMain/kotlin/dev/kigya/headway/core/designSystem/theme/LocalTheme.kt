package dev.kigya.headway.core.designSystem.theme

import androidx.compose.runtime.staticCompositionLocalOf
import kotlin.jvm.JvmInline

@JvmInline
value class Theme private constructor(val isDark: Boolean = false) {
    internal companion object {
        fun create(isDark: Boolean) = Theme(isDark)
    }
}

val LocalTheme = staticCompositionLocalOf { Theme.create(false) }
