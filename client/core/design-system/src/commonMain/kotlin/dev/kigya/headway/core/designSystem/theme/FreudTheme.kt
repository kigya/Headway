package dev.kigya.headway.core.designSystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.compose.LocalPlatformContext
import coil3.network.ktor3.KtorNetworkFetcherFactory
import dev.kigya.headway.core.designSystem.component.LocalFreudImageLoader
import dev.kigya.headway.core.designSystem.theme.color.FreudColor
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.dimension.FreudDimension
import dev.kigya.headway.core.designSystem.theme.shape.FreudShape
import dev.kigya.headway.core.designSystem.theme.text.FreudFont
import dev.kigya.headway.core.designSystem.theme.text.FreudFontWeight
import dev.kigya.headway.core.designSystem.theme.text.FreudTextSize
import dev.kigya.headway.core.designSystem.theme.text.FreudTypography
import kotlin.jvm.JvmInline

@Composable
fun FreudTheme(
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val platformContext = LocalPlatformContext.current
    val imageLoader = remember(platformContext) {
        ImageLoader.Builder(platformContext)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .build()
    }
    CompositionLocalProvider(
        values = arrayOf(
            LocalTheme provides Theme.create(isDark = isDark),
            LocalFreudImageLoader provides imageLoader,
        ),
        content = content,
    )
}

@JvmInline
value class FreudDsToken<T>(val value: T)

abstract class FreudTheme {

    internal object DefaultFreudTheme : FreudTheme()

    val font = FreudFont
    val shape = FreudShape
    val dimension = FreudDimension
    val typography = FreudTypography
    val textSize = FreudTextSize
    val colorScheme = FreudColorScheme()

    protected val fontWeight = FreudFontWeight
    protected val color = FreudColor()
}
