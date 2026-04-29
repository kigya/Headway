package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.kigya.headway.core.designSystem.component.FreudAsyncImage
import dev.kigya.headway.core.designSystem.component.FreudAsyncImageShape
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudAsyncImagePreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudAsyncImagePreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudAsyncImagePreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue

private object FreudAsyncImagePreviewTheme : FreudTheme() {
    val FreudColorScheme.surface
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.block
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.text
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray100,
            dark = super.color.brown10,
        )
}

private enum class FreudAsyncImagePreviewShapeKind {
    CIRCLE,
    ROUNDED,
}

private data class FreudAsyncImagePreviewCase(
    val isDark: Boolean,
    val shapeKind: FreudAsyncImagePreviewShapeKind,
    val isEmptyUrl: Boolean,
)

private class FreudAsyncImagePreviewCaseProvider : PreviewParameterProvider<FreudAsyncImagePreviewCase> {
    override val values: Sequence<FreudAsyncImagePreviewCase> = sequence {
        val themes = listOf(false, true)
        val shapes = FreudAsyncImagePreviewShapeKind.entries
        val emptyFlags = listOf(false, true)
        for (isDark in themes) {
            for (shapeKind in shapes) {
                for (isEmptyUrl in emptyFlags) {
                    yield(FreudAsyncImagePreviewCase(isDark, shapeKind, isEmptyUrl))
                }
            }
        }
    }
}

@Preview(
    name = "FreudAsyncImage – Theme × Shape × Empty URL",
    showBackground = false,
)
@Composable
private fun FreudAsyncImagePreview(
    @PreviewParameter(FreudAsyncImagePreviewCaseProvider::class) case: FreudAsyncImagePreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme
        val shape = when (case.shapeKind) {
            FreudAsyncImagePreviewShapeKind.CIRCLE -> FreudAsyncImageShape.Circle
            FreudAsyncImagePreviewShapeKind.ROUNDED -> FreudAsyncImageShape.RoundedRectangle(ds.dimension.dp8)
        }
        val label = buildString {
            append(if (case.isDark) PREVIEW_LABEL_DARK else PREVIEW_LABEL_LIGHT)
            append(PREVIEW_LABEL_SEPARATOR)
            append(if (case.shapeKind == FreudAsyncImagePreviewShapeKind.CIRCLE) "Circle" else "Rounded")
            append(PREVIEW_LABEL_SEPARATOR)
            append(if (case.isEmptyUrl) "Fallback" else "Remote")
        }
        Column(
            modifier = Modifier
                .background(FreudAsyncImagePreviewTheme.colorScheme.surface.value)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = FreudTextValue.text(label),
                color = FreudAsyncImagePreviewTheme.colorScheme.text,
                typography = ds.typography.textSmSemiBold,
            )
            Box(
                modifier = Modifier
                    .padding(top = ds.dimension.dp12.value)
                    .background(FreudAsyncImagePreviewTheme.colorScheme.block.value)
                    .padding(ds.dimension.dp16.value),
            ) {
                FreudAsyncImage(
                    imageUrl = if (case.isEmptyUrl) null else SAMPLE_REMOTE_IMAGE_URL,
                    contentDescription = null,
                    shape = shape,
                    modifier = Modifier.size(ds.dimension.dp128.value),
                    contentScale = ContentScale.Crop,
                )
            }
        }
    }
}

private const val PREVIEW_LABEL_DARK = "Dark"

private const val PREVIEW_LABEL_LIGHT = "Light"

private const val PREVIEW_LABEL_SEPARATOR = " · "

private const val SAMPLE_REMOTE_IMAGE_URL = "https://picsum.photos/seed/headway-ds-async/240/240"
