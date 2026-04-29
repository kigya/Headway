package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.avatarRing
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.cardSurface
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.mentorAccent
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.mentorPillSurface
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.primaryText
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.supportingText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.background
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_calendar
import headway.core.design_system.generated.resources.ic_readiness
import headway.core.design_system.generated.resources.ic_session_type
import headway.core.design_system.generated.resources.ic_star_outline
import org.jetbrains.compose.resources.DrawableResource

@Immutable
data class FreudHomeHeaderContent(
    val greeting: FreudTextValue,
    val dateLabel: FreudTextValue,
    val roleLabel: FreudTextValue?,
    val metrics: FreudHomeHeaderMetrics?,
)

@Immutable
data class FreudHomeHeaderMetrics(
    val readinessLine: FreudTextValue?,
    val nextSessionLine: FreudTextValue?,
)

@Composable
fun FreudHomeHeader(
    content: FreudHomeHeaderContent,
    avatarImageUrl: String?,
    avatarContentDescription: String?,
    modifier: Modifier = Modifier,
    isWideLayout: Boolean = false,
) {
    val ds = FreudTheme.DefaultFreudTheme
    val cornerRadius24 = ds.dimension.dp24.value
    val headerShape = if (isWideLayout) {
        RoundedCornerShape(
            topStart = ds.dimension.dp16.value,
            topEnd = ds.dimension.dp48.value,
            bottomEnd = cornerRadius24,
            bottomStart = cornerRadius24,
        )
    } else {
        RoundedCornerShape(cornerRadius24)
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = FreudHomeHeaderTheme.colorScheme.cardSurface,
                pattern = FreudBackgroundPattern.Waves,
                shape = headerShape,
            )
            .clip(headerShape)
            .padding(FreudHomeHeaderDefaults.horizontalPadding.value),
        verticalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.blockSpacing.value),
    ) {
        if (isWideLayout) {
            FreudHomeHeaderWideBody(
                content = content,
                avatarImageUrl = avatarImageUrl,
                avatarContentDescription = avatarContentDescription,
            )
        } else {
            FreudHomeHeaderNarrowBody(
                content = content,
                avatarImageUrl = avatarImageUrl,
                avatarContentDescription = avatarContentDescription,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderNarrowBody(
    content: FreudHomeHeaderContent,
    avatarImageUrl: String?,
    avatarContentDescription: String?,
) {
    FreudHomeHeaderDateRow(dateLabel = content.dateLabel)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.avatarSpacing.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudHomeHeaderAvatar(avatarImageUrl = avatarImageUrl, avatarContentDescription = avatarContentDescription)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.greetingToChipsSpacing.value),
        ) {
            FreudText(
                value = content.greeting,
                color = FreudHomeHeaderTheme.colorScheme.primaryText,
                typography = FreudHomeHeaderTheme.typography.headingSmExtraBold,
            )
            FreudHomeHeaderChipsRow(
                roleLabel = content.roleLabel,
                metrics = content.metrics,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderWideBody(
    content: FreudHomeHeaderContent,
    avatarImageUrl: String?,
    avatarContentDescription: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.avatarSpacing.value),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FreudHomeHeaderAvatar(avatarImageUrl = avatarImageUrl, avatarContentDescription = avatarContentDescription)
            FreudText(
                value = content.greeting,
                color = FreudHomeHeaderTheme.colorScheme.primaryText,
                typography = FreudHomeHeaderTheme.typography.headingSmExtraBold,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.blockSpacing.value),
        ) {
            FreudHomeHeaderDateRow(dateLabel = content.dateLabel)
            FreudHomeHeaderChipsRow(
                roleLabel = content.roleLabel,
                metrics = content.metrics,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderAvatar(
    avatarImageUrl: String?,
    avatarContentDescription: String?,
) {
    Box(
        modifier = Modifier
            .size(FreudHomeHeaderDefaults.avatarOuterSize.value)
            .border(
                width = FreudHomeHeaderDefaults.avatarRingWidth.value,
                color = FreudHomeHeaderTheme.colorScheme.avatarRing.value,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        FreudAsyncImage(
            imageUrl = avatarImageUrl,
            contentDescription = avatarContentDescription,
            shape = FreudAsyncImageShape.Circle,
            modifier = Modifier.size(FreudHomeHeaderDefaults.avatarSize.value),
        )
    }
}

@Composable
private fun FreudHomeHeaderDateRow(dateLabel: FreudTextValue) {
    val ds = FreudTheme.DefaultFreudTheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(ds.dimension.dp8.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudIcon(
            resource = Res.drawable.ic_calendar,
            contentDescription = null,
            size = FreudHomeHeaderDefaults.inlineIconSize,
            tint = FreudHomeHeaderTheme.colorScheme.primaryText,
        )
        FreudText(
            value = dateLabel,
            color = FreudHomeHeaderTheme.colorScheme.supportingText,
            typography = FreudHomeHeaderTheme.typography.textMdSemiBold,
        )
    }
}

@Composable
private fun FreudHomeHeaderChipsRow(
    roleLabel: FreudTextValue?,
    metrics: FreudHomeHeaderMetrics?,
) {
    val readiness = metrics?.readinessLine
    val nextSession = metrics?.nextSessionLine
    if (roleLabel == null && readiness == null && nextSession == null) {
        return
    }
    val ds = FreudTheme.DefaultFreudTheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(ds.dimension.dp8.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        roleLabel?.let { label ->
            FreudHomeHeaderRolePill(label = label)
        }
        readiness?.let { line ->
            FreudHomeHeaderMetricChip(
                icon = Res.drawable.ic_readiness,
                value = line,
            )
        }
        nextSession?.let { line ->
            FreudHomeHeaderMetricChip(
                icon = Res.drawable.ic_session_type,
                value = line,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderRolePill(label: FreudTextValue) {
    val ds = FreudTheme.DefaultFreudTheme
    Row(
        modifier = Modifier
            .clip(ds.shape.rounding24.value)
            .background(
                color = FreudHomeHeaderTheme.colorScheme.mentorPillSurface,
                pattern = FreudBackgroundPattern.None,
                shape = ds.shape.rounding24.value,
            )
            .padding(
                horizontal = ds.dimension.dp12.value,
                vertical = ds.dimension.dp4.value,
            ),
        horizontalArrangement = Arrangement.spacedBy(ds.dimension.dp4.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudIcon(
            resource = Res.drawable.ic_star_outline,
            contentDescription = null,
            size = FreudHomeHeaderDefaults.inlineIconSize,
            tint = null,
        )
        FreudText(
            value = label,
            color = FreudHomeHeaderTheme.colorScheme.mentorAccent,
            typography = FreudHomeHeaderTheme.typography.textSmSemiBold,
        )
    }
}

@Composable
private fun FreudHomeHeaderMetricChip(
    icon: DrawableResource,
    value: FreudTextValue,
) {
    val ds = FreudTheme.DefaultFreudTheme
    Row(
        horizontalArrangement = Arrangement.spacedBy(ds.dimension.dp4.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudIcon(
            resource = icon,
            contentDescription = null,
            size = FreudHomeHeaderDefaults.inlineIconSize,
            tint = null,
        )
        FreudText(
            value = value,
            color = FreudHomeHeaderTheme.colorScheme.primaryText,
            typography = FreudHomeHeaderTheme.typography.textMdSemiBold,
        )
    }
}

private object FreudHomeHeaderDefaults {
    val horizontalPadding = FreudTheme.DefaultFreudTheme.dimension.dp20
    val blockSpacing = FreudTheme.DefaultFreudTheme.dimension.dp16
    val avatarSpacing = FreudTheme.DefaultFreudTheme.dimension.dp16
    val greetingToChipsSpacing = FreudTheme.DefaultFreudTheme.dimension.dp12
    val avatarSize = FreudTheme.DefaultFreudTheme.dimension.dp64
    val avatarRingWidth = FreudTheme.DefaultFreudTheme.dimension.dp2
    val avatarOuterSize = FreudTheme.DefaultFreudTheme.dimension.dp72
    val inlineIconSize = FreudTheme.DefaultFreudTheme.dimension.dp20
}

@Suppress("TopLevelComposableFunctions")
internal object FreudHomeHeaderTheme : FreudTheme() {
    val FreudColorScheme.cardSurface: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown90,
        )

    val FreudColorScheme.primaryText: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown90,
            dark = super.color.brown40,
        )

    val FreudColorScheme.supportingText: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown70,
            dark = super.color.brown50,
        )

    val FreudColorScheme.avatarRing: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.brown30,
            dark = super.color.brown60,
        )

    val FreudColorScheme.mentorPillSurface: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.green10,
            dark = super.color.green90,
        )

    val FreudColorScheme.mentorAccent: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = super.color.green50,
            dark = super.color.green40,
        )
}
