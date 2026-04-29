package dev.kigya.headway.core.designSystem.component

import androidx.compose.runtime.staticCompositionLocalOf
import coil3.ImageLoader

internal val LocalFreudImageLoader = staticCompositionLocalOf<ImageLoader> {
    error("FreudTheme must wrap content that uses FreudAsyncImage.")
}
