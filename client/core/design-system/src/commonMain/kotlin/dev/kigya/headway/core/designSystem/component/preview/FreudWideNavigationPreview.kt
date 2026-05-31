package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.component.FreudIcon
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.FreudWideNavigation
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationItem
import dev.kigya.headway.core.designSystem.component.preview.FreudWideNavigationPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudWideNavigationPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudWideNavigationPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_calendar
import headway.core.design_system.generated.resources.ic_chevron_left
import headway.core.design_system.generated.resources.ic_el_baion
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

private object FreudWideNavigationPreviewTheme : FreudTheme() {
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

private fun previewWideNavigationItems(): ImmutableList<FreudWideNavigationItem> = persistentListOf(
    FreudWideNavigationItem(
        key = PREVIEW_NAV_KEY_HOME,
        label = FreudTextValue.text("Home"),
        iconUrl = "",
        iconResource = Res.drawable.ic_el_baion,
    ),
    FreudWideNavigationItem(
        key = PREVIEW_NAV_KEY_PREP,
        label = FreudTextValue.text("Preparation"),
        iconUrl = "",
        iconResource = Res.drawable.ic_calendar,
    ),
)

private data class FreudWideNavigationPreviewCase(
    val isDark: Boolean,
    val isRailVisible: Boolean,
    val selectedKey: String?,
)

private class FreudWideNavigationPreviewCaseProvider : PreviewParameterProvider<FreudWideNavigationPreviewCase> {
    override val values: Sequence<FreudWideNavigationPreviewCase> = sequence {
        val themes = listOf(false, true)
        val railFlags = listOf(false, true)
        val selections = listOf(PREVIEW_NAV_KEY_HOME, null)
        for (isDark in themes) {
            for (isRailVisible in railFlags) {
                for (selectedKey in selections) {
                    yield(FreudWideNavigationPreviewCase(isDark, isRailVisible, selectedKey))
                }
            }
        }
    }
}

@Preview(
    name = "FreudWideNavigation – Theme × Rail × Selection",
    showBackground = false,
)
@Composable
private fun FreudWideNavigationPreview(
    @PreviewParameter(FreudWideNavigationPreviewCaseProvider::class) case: FreudWideNavigationPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme
        val label = buildString {
            append(if (case.isDark) PREVIEW_LABEL_DARK else PREVIEW_LABEL_LIGHT)
            append(PREVIEW_LABEL_SEPARATOR)
            append(if (case.isRailVisible) PREVIEW_RAIL_VISIBLE_LABEL else PREVIEW_RAIL_COLLAPSED_LABEL)
            append(PREVIEW_LABEL_SEPARATOR)
            append(case.selectedKey ?: PREVIEW_SELECTION_NONE_LABEL)
        }
        Column(
            modifier = Modifier
                .background(FreudWideNavigationPreviewTheme.colorScheme.surface.value)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = FreudTextValue.text(label),
                color = FreudWideNavigationPreviewTheme.colorScheme.text,
                typography = ds.typography.textSmSemiBold,
            )
            FreudWideNavigation(
                items = previewWideNavigationItems(),
                selectedKey = case.selectedKey,
                isRailVisible = case.isRailVisible,
                onToggleRail = {},
                onItemClick = {},
                headerLeadingContent = {
                    FreudText(
                        value = FreudTextValue.text("headway"),
                        color = FreudWideNavigationPreviewTheme.colorScheme.text,
                        typography = ds.typography.textMdSemiBold,
                    )
                },
                headerTrailingContent = {
                    Row {
                        FreudIcon(
                            resource = Res.drawable.ic_chevron_left,
                            contentDescription = null,
                            modifier = Modifier.size(ds.dimension.dp16.value),
                            tint = FreudWideNavigationPreviewTheme.colorScheme.text,
                        )
                        FreudIcon(
                            resource = Res.drawable.ic_chevron_left,
                            contentDescription = null,
                            modifier = Modifier.size(ds.dimension.dp16.value),
                            tint = FreudWideNavigationPreviewTheme.colorScheme.text,
                        )
                    }
                },
                modifier = Modifier
                    .padding(top = ds.dimension.dp12.value)
                    .fillMaxWidth()
                    .height(PREVIEW_WIDE_NAV_HEIGHT_DP.dp),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(FreudWideNavigationPreviewTheme.colorScheme.block.value),
                        contentAlignment = Alignment.Center,
                    ) {
                        FreudText(
                            value = FreudTextValue.text(PREVIEW_WIDE_NAV_CONTENT_LABEL),
                            color = FreudWideNavigationPreviewTheme.colorScheme.text,
                            typography = ds.typography.textMdSemiBold,
                            align = TextAlign.Center,
                        )
                    }
                },
            )
        }
    }
}

private const val PREVIEW_NAV_KEY_HOME = "home"

private const val PREVIEW_NAV_KEY_PREP = "prep"

private const val PREVIEW_LABEL_DARK = "Dark"

private const val PREVIEW_LABEL_LIGHT = "Light"

private const val PREVIEW_LABEL_SEPARATOR = " · "

private const val PREVIEW_RAIL_VISIBLE_LABEL = "Rail"

private const val PREVIEW_RAIL_COLLAPSED_LABEL = "Collapsed"

private const val PREVIEW_SELECTION_NONE_LABEL = "none"

private const val PREVIEW_WIDE_NAV_CONTENT_LABEL = "Content"

private const val PREVIEW_WIDE_NAV_HEIGHT_DP = 400
