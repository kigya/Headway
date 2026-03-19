package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.FreudTopBar
import dev.kigya.headway.core.designSystem.component.FreudTopBarEndSlot
import dev.kigya.headway.core.designSystem.component.FreudTopBarStartSlot
import dev.kigya.headway.core.designSystem.component.preview.FreudTopBarPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudTopBarPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudTopBarPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides

private object FreudTopBarPreviewTheme : FreudTheme() {

    val FreudColorScheme.surface: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.block: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.text: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray100,
            dark = super.color.brown10,
        )
}

private enum class FreudTopBarPreviewWidth {
    NARROW,
    WIDE,
}

private data class FreudTopBarPreviewCase(
    val isDark: Boolean,
    val width: FreudTopBarPreviewWidth,
    val title: String?,
    val hasStartSlot: Boolean,
    val hasEndSlot: Boolean,
)

private class FreudTopBarPreviewCaseProvider : PreviewParameterProvider<FreudTopBarPreviewCase> {
    override val values: Sequence<FreudTopBarPreviewCase> = sequence {
        val baseCases = listOf(
            FreudTopBarPreviewCase(
                isDark = false,
                width = FreudTopBarPreviewWidth.NARROW,
                title = "Manage Invitations",
                hasStartSlot = true,
                hasEndSlot = false,
            ),
            FreudTopBarPreviewCase(
                isDark = false,
                width = FreudTopBarPreviewWidth.WIDE,
                title = "Check",
                hasStartSlot = true,
                hasEndSlot = true,
            ),
            FreudTopBarPreviewCase(
                isDark = false,
                width = FreudTopBarPreviewWidth.NARROW,
                title = "Very long top bar title that should be ellipsized",
                hasStartSlot = true,
                hasEndSlot = true,
            ),
            FreudTopBarPreviewCase(
                isDark = false,
                width = FreudTopBarPreviewWidth.WIDE,
                title = null,
                hasStartSlot = true,
                hasEndSlot = true,
            ),
        )

        for (isDark in listOf(false, true)) {
            for (case in baseCases) {
                yield(case.copy(isDark = isDark))
            }
        }
    }
}

@Preview(
    name = "FreudTopBar – Theme x Width x Slots",
    showBackground = false,
)
@Composable
private fun FreudTopBarPreview(
    @PreviewParameter(FreudTopBarPreviewCaseProvider::class) case: FreudTopBarPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudTopBarPreviewTheme.colorScheme.surface.value
        val block = FreudTopBarPreviewTheme.colorScheme.block.value
        val text = FreudTopBarPreviewTheme.colorScheme.text

        val themeLabel = if (case.isDark) "Dark" else "Light"
        val widthLabel = when (case.width) {
            FreudTopBarPreviewWidth.NARROW -> "Narrow"
            FreudTopBarPreviewWidth.WIDE -> "Wide"
        }
        val titleLabel = when {
            case.title == null -> "title=null"
            case.title.length > 24 -> "title=long"
            else -> "title=short"
        }
        val header = buildString {
            append(themeLabel)
            append(" • ")
            append(widthLabel)
            append(" • ")
            append(if (case.hasStartSlot) "start=back" else "start=null")
            append(" • ")
            append(if (case.hasEndSlot) "end=signOut" else "end=null")
            append(" • ")
            append(titleLabel)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(surface)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = header,
                color = text,
                typography = ds.typography.labelSm,
                align = TextAlign.Start,
                maxLines = 2,
                modifier = Modifier.fillMaxWidth(),
            )

            FreudSpacer(size = ds.dimension.dp12)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = block,
                        shape = ds.shape.rounding24.value,
                    )
                    .padding(ds.dimension.dp16.value),
                contentAlignment = Alignment.Center,
            ) {
                FreudTopBar(
                    modifier = when (case.width) {
                        FreudTopBarPreviewWidth.NARROW -> Modifier.width(ds.dimension.dp248.value)
                        FreudTopBarPreviewWidth.WIDE -> Modifier.fillMaxWidth()
                    },
                    title = case.title,
                    startSlot = if (case.hasStartSlot) {
                        FreudTopBarStartSlot.Back(onClick = {})
                    } else {
                        null
                    },
                    endSlot = if (case.hasEndSlot) {
                        FreudTopBarEndSlot.SignOut(onClick = {})
                    } else {
                        null
                    },
                )
            }
        }
    }
}
