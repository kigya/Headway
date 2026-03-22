package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.component.FreudAnimatedIcon
import dev.kigya.headway.core.designSystem.component.FreudIcon
import dev.kigya.headway.core.designSystem.component.FreudIconDefaults
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudIconPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudIconPreviewTheme.iconTint
import dev.kigya.headway.core.designSystem.component.preview.FreudIconPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudIconPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.previewPixelGrid
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_el_baion
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource

private object FreudIconPreviewTheme : FreudTheme() {

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

    val FreudColorScheme.iconTint
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown30,
            dark = super.color.orange10,
        )
}

private enum class FreudIconPreviewKind {
    STATIC,
    ANIM_FADE_IN_SCALE,
}

private data class FreudIconPreviewCase(
    val isDark: Boolean,
    val kind: FreudIconPreviewKind,
    val size: FreudDsToken<Dp>?,
    val tintEnabled: Boolean,
)

private class FreudIconPreviewCaseProvider : PreviewParameterProvider<FreudIconPreviewCase> {
    override val values: Sequence<FreudIconPreviewCase> = sequence {
        val themes = listOf(false, true)
        val kinds = listOf(
            FreudIconPreviewKind.STATIC,
            FreudIconPreviewKind.ANIM_FADE_IN_SCALE,
        )
        val sizes = listOf(
            null,
            FreudTheme.DefaultFreudTheme.dimension.dp24,
            FreudTheme.DefaultFreudTheme.dimension.dp48,
        )
        val tints = listOf(false, true)

        for (isDark in themes) {
            for (kind in kinds) {
                for (size in sizes) {
                    for (tintEnabled in tints) {
                        yield(FreudIconPreviewCase(isDark, kind, size, tintEnabled))
                    }
                }
            }
        }
    }
}

@Preview(
    name = "FreudIcon – Theme x Kind x Size x Tint",
    showBackground = false,
)
@Composable
private fun FreudIconPreview(
    @PreviewParameter(FreudIconPreviewCaseProvider::class) case: FreudIconPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudIconPreviewTheme.colorScheme.surface.value
        val block = FreudIconPreviewTheme.colorScheme.block.value
        val text = FreudIconPreviewTheme.colorScheme.text
        val tint = if (case.tintEnabled) FreudIconPreviewTheme.colorScheme.iconTint else null

        val themeLabel = if (case.isDark) "Dark" else "Light"
        val kindLabel = when (case.kind) {
            FreudIconPreviewKind.STATIC -> "Static"
            FreudIconPreviewKind.ANIM_FADE_IN_SCALE -> "Animated • FadeInScale"
        }
        val sizeLabel = case.size?.value?.toString() ?: "null"
        val tintLabel = if (case.tintEnabled) "tint=on" else "tint=off"
        val header = "$themeLabel • $kindLabel • size=$sizeLabel • $tintLabel"

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                FreudText(
                    value = FreudTextValue.text(header),
                    color = text,
                    typography = ds.typography.labelSm,
                    align = TextAlign.Start,
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                )

                Box(modifier = Modifier.height(ds.dimension.dp12.value))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(block)
                        .padding(ds.dimension.dp12.value),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    when (case.kind) {
                        FreudIconPreviewKind.STATIC -> FreudIcon(
                            resource = PreviewIconRes,
                            contentDescription = null,
                            size = case.size,
                            tint = tint,
                            modifier = Modifier.previewPixelGrid(),
                        )

                        FreudIconPreviewKind.ANIM_FADE_IN_SCALE -> TogglingAnimatedIcon(
                            resource = PreviewIconRes,
                            contentDescription = null,
                            size = case.size,
                            tint = tint,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TogglingAnimatedIcon(
    resource: DrawableResource,
    contentDescription: String?,
    size: FreudDsToken<Dp>?,
    tint: FreudDsToken<Color>?,
) {
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(ANIMATED_ICON_DELAY) {
        while (true) {
            delay(ANIMATED_ICON_DELAY)
            isVisible = !isVisible
        }
    }

    FreudAnimatedIcon(
        resource = resource,
        contentDescription = contentDescription,
        isVisible = isVisible,
        size = size,
        tint = tint,
        animation = FreudIconDefaults.fadeInScale(),
        modifier = Modifier.previewPixelGrid(),
    )
}

private const val ANIMATED_ICON_DELAY = 900L
private val PreviewIconRes: DrawableResource = Res.drawable.ic_el_baion
