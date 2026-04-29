package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import dev.kigya.headway.core.designSystem.component.FreudAsyncImageFallbackTheme.fallbackFill
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_el_baion
import org.jetbrains.compose.resources.painterResource

@Immutable
sealed interface FreudAsyncImageShape {
    data object Circle : FreudAsyncImageShape

    @Immutable
    data class RoundedRectangle(
        val cornerToken: FreudDsToken<Dp>,
    ) : FreudAsyncImageShape
}

@Composable
fun FreudAsyncImage(
    imageUrl: String?,
    contentDescription: String?,
    shape: FreudAsyncImageShape,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    val resolvedShape: Shape = when (shape) {
        FreudAsyncImageShape.Circle -> CircleShape
        is FreudAsyncImageShape.RoundedRectangle -> RoundedCornerShape(shape.cornerToken.value)
    }
    val platformContext = LocalPlatformContext.current
    val imageLoader = LocalFreudImageLoader.current
    val trimmed = imageUrl?.trim()
    if (trimmed.isNullOrEmpty()) {
        FreudAsyncImageFallback(
            resolvedShape = resolvedShape,
            modifier = modifier,
        )
        return
    }
    val placeholderPainter = painterResource(Res.drawable.ic_el_baion)
    val request = ImageRequest.Builder(platformContext)
        .data(trimmed)
        .build()
    AsyncImage(
        model = request,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier.clip(resolvedShape),
        contentScale = contentScale,
        placeholder = placeholderPainter,
        error = placeholderPainter,
    )
}

@Composable
private fun FreudAsyncImageFallback(
    resolvedShape: Shape,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(resolvedShape)
            .background(FreudAsyncImageFallbackTheme.colorScheme.fallbackFill.value),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_el_baion),
            contentDescription = null,
            modifier = Modifier.size(FreudTheme.DefaultFreudTheme.dimension.dp48.value),
        )
    }
}

internal object FreudAsyncImageFallbackTheme : FreudTheme() {
    val FreudColorScheme.fallbackFill
        @Composable
        get() = this provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.gray80,
        )
}
