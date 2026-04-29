package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationTheme.railBackground
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationTheme.railItemDefault
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationTheme.railItemSelected
import dev.kigya.headway.core.designSystem.component.FreudWideNavigationTheme.railItemText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class FreudWideNavigationItem(
    val key: String,
    val label: FreudTextValue,
    val iconUrl: String,
)

@Composable
fun FreudWideNavigation(
    items: ImmutableList<FreudWideNavigationItem>,
    selectedKey: String?,
    isRailVisible: Boolean,
    onToggleRail: () -> Unit,
    onItemClick: (FreudWideNavigationItem) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        AnimatedVisibility(
            visible = isRailVisible,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = FreudWideNavigationDefaults.railMaxWidth.value)
                    .fillMaxHeight()
                    .background(FreudWideNavigationTheme.colorScheme.railBackground.value)
                    .padding(FreudWideNavigationDefaults.railPadding.value),
                verticalArrangement = Arrangement.spacedBy(FreudWideNavigationDefaults.itemSpacing.value),
            ) {
                items.forEach { item ->
                    FreudWideNavigationRow(
                        item = item,
                        isSelected = item.key == selectedKey,
                        onClick = { onItemClick(item) },
                    )
                }
                FreudWideNavigationChromeButton(
                    label = FreudTextValue.text("Hide menu"),
                    onClick = onToggleRail,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            AnimatedVisibility(
                visible = !isRailVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FreudWideNavigationChromeButton(
                    label = FreudTextValue.text("Menu"),
                    onClick = onToggleRail,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(FreudWideNavigationDefaults.revealPadding.value),
                )
            }
            content()
        }
    }
}

@Composable
private fun FreudWideNavigationRow(
    item: FreudWideNavigationItem,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val shape = RoundedCornerShape(FreudWideNavigationDefaults.rowCorner.value)
    val backgroundToken = if (isSelected) {
        FreudWideNavigationTheme.colorScheme.railItemSelected
    } else {
        FreudWideNavigationTheme.colorScheme.railItemDefault
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(backgroundToken.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(FreudWideNavigationDefaults.rowPadding.value),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FreudWideNavigationDefaults.rowGap.value),
    ) {
        FreudAsyncImage(
            imageUrl = item.iconUrl,
            contentDescription = null,
            shape = FreudAsyncImageShape.RoundedRectangle(FreudTheme.DefaultFreudTheme.dimension.dp8),
            modifier = Modifier.size(FreudWideNavigationDefaults.iconBox.value),
            contentScale = ContentScale.Fit,
        )
        FreudText(
            value = item.label,
            color = FreudWideNavigationTheme.colorScheme.railItemText,
            typography = FreudWideNavigationTheme.typography.textMdSemiBold,
            align = TextAlign.Start,
        )
    }
}

@Composable
private fun FreudWideNavigationChromeButton(
    label: FreudTextValue,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FreudHorizontalButton(
        text = label,
        onClick = onClick,
        containerColor = FreudWideNavigationTheme.colorScheme.railItemDefault,
        contentColor = FreudWideNavigationTheme.colorScheme.railItemText,
        size = FreudHorizontalButtonSize.SMALL,
        modifier = modifier,
    )
}

private object FreudWideNavigationDefaults {
    val railMaxWidth = FreudTheme.DefaultFreudTheme.dimension.dp248
    val railPadding = FreudTheme.DefaultFreudTheme.dimension.dp12
    val itemSpacing = FreudTheme.DefaultFreudTheme.dimension.dp8
    val revealPadding = FreudTheme.DefaultFreudTheme.dimension.dp8
    val rowPadding = FreudTheme.DefaultFreudTheme.dimension.dp12
    val rowGap = FreudTheme.DefaultFreudTheme.dimension.dp12
    val rowCorner = FreudTheme.DefaultFreudTheme.dimension.dp12
    val iconBox = FreudTheme.DefaultFreudTheme.dimension.dp48
}

@Suppress("TopLevelComposableFunctions")
internal object FreudWideNavigationTheme : FreudTheme() {
    val FreudColorScheme.railBackground: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.gray10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.railItemSelected: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown30,
            dark = super.color.brown50,
        )

    val FreudColorScheme.railItemDefault: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.gray20,
            dark = super.color.brown80,
        )

    val FreudColorScheme.railItemText: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown90,
            dark = super.color.brown40,
        )
}
