package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.cardSurface
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.dateLabelText
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.mentorAccent
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.mentorPillSurface
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderTheme.primaryText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.core.designSystem.util.isWide
import dev.kigya.headway.core.designSystem.util.resolveAnnotatedString
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_calendar
import headway.core.design_system.generated.resources.ic_readiness
import headway.core.design_system.generated.resources.ic_session_type
import headway.core.design_system.generated.resources.ic_star_solid
import org.jetbrains.compose.resources.DrawableResource

private object FreudHomeHeaderDefaults {
    const val GREETING_MAX_LINES: Int = 1

    const val DATE_LABEL_TEXT_ALPHA_LIGHT: Float = 0.64f

    const val DATE_LABEL_TEXT_ALPHA_DARK: Float = 0.8f

    val blockSpacing = FreudTheme.DefaultFreudTheme.dimension.dp12
    val avatarSpacing = FreudTheme.DefaultFreudTheme.dimension.dp12
    val greetingToChipsSpacing = FreudTheme.DefaultFreudTheme.dimension.dp4
    val avatarSize = FreudTheme.DefaultFreudTheme.dimension.dp64
    val inlineIconSize = FreudTheme.DefaultFreudTheme.dimension.dp20
}

@Immutable
data class FreudHomeHeaderContent(
    val greetingPrimary: FreudTextValue,
    val greetingShortIfOverflow: FreudTextValue?,
    val dateLabel: FreudTextValue,
    val roleLabel: FreudTextValue?,
    val metrics: FreudHomeHeaderMetrics?,
    val avatarImageUrl: String?,
)

@Immutable
data class FreudHomeHeaderMetrics(
    val readinessLine: FreudTextValue?,
    val nextSessionLine: FreudTextValue?,
)

@Composable
fun FreudHomeHeader(
    content: FreudHomeHeaderContent,
    avatarContentDescription: String?,
    modifier: Modifier = Modifier,
) {
    val ds = FreudTheme.DefaultFreudTheme
    val cornerRadius4 = ds.dimension.dp4.value
    val headerShape = if (isWide()) {
        RoundedCornerShape(
            topStart = ds.dimension.dp16.value,
            topEnd = ds.dimension.dp48.value,
            bottomEnd = cornerRadius4,
            bottomStart = cornerRadius4,
        )
    } else {
        RoundedCornerShape(cornerRadius4)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = FreudHomeHeaderTheme.colorScheme.cardSurface,
                pattern = FreudBackgroundPattern.Waves,
                shape = headerShape,
            )
            .clip(headerShape),
        verticalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.blockSpacing.value),
    ) {
        if (isWide()) {
            FreudHomeHeaderWideBody(
                content = content,
                avatarContentDescription = avatarContentDescription,
            )
        } else {
            FreudHomeHeaderNarrowBody(
                content = content,
                avatarContentDescription = avatarContentDescription,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderNarrowBody(
    content: FreudHomeHeaderContent,
    avatarContentDescription: String?,
) {
    FreudHomeHeaderDateRow(dateLabel = content.dateLabel)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.avatarSpacing.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudHomeHeaderAvatar(
            avatarImageUrl = content.avatarImageUrl,
            avatarContentDescription = avatarContentDescription,
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.greetingToChipsSpacing.value),
        ) {
            FreudHomeHeaderGreeting(
                greetingPrimary = content.greetingPrimary,
                greetingShortIfOverflow = content.greetingShortIfOverflow,
                color = FreudHomeHeaderTheme.colorScheme.primaryText,
                typography = FreudHomeHeaderTheme.typography.headingSmExtraBold,
                modifier = Modifier.fillMaxWidth(),
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
    avatarContentDescription: String?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(FreudHomeHeaderDefaults.avatarSpacing.value),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FreudHomeHeaderAvatar(
                avatarImageUrl = content.avatarImageUrl,
                avatarContentDescription = avatarContentDescription,
            )
            FreudHomeHeaderGreeting(
                greetingPrimary = content.greetingPrimary,
                greetingShortIfOverflow = content.greetingShortIfOverflow,
                color = FreudHomeHeaderTheme.colorScheme.primaryText,
                typography = FreudHomeHeaderTheme.typography.headingSmExtraBold,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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
private fun FreudHomeHeaderGreeting(
    greetingPrimary: FreudTextValue,
    greetingShortIfOverflow: FreudTextValue?,
    color: FreudDsToken<Color>,
    typography: FreudDsToken<TextStyle>,
    modifier: Modifier = Modifier,
) {
    val maxLines = FreudHomeHeaderDefaults.GREETING_MAX_LINES
    if (greetingShortIfOverflow == null) {
        FreudText(
            value = greetingPrimary,
            color = color,
            typography = typography,
            modifier = modifier.fillMaxWidth(),
            align = TextAlign.Start,
            maxLines = maxLines,
        )
    } else {
        val fullAnnotated = greetingPrimary.resolveAnnotatedString(defaultContentColor = color)
        val textMeasurer = rememberTextMeasurer()
        val style = typography.value
        val layoutDirection = LocalLayoutDirection.current
        BoxWithConstraints(modifier = modifier) {
            val parentConstraints = constraints
            val useShort = parentConstraints.hasBoundedWidth &&
                parentConstraints.maxWidth > 0 &&
                textMeasurer.measure(
                    text = fullAnnotated,
                    style = style,
                    overflow = TextOverflow.Clip,
                    softWrap = true,
                    maxLines = maxLines,
                    constraints = parentConstraints,
                    layoutDirection = layoutDirection,
                ).hasVisualOverflow
            val value = if (useShort) {
                greetingShortIfOverflow
            } else {
                greetingPrimary
            }
            FreudText(
                value = value,
                color = color,
                typography = typography,
                modifier = Modifier.fillMaxWidth(),
                align = TextAlign.Start,
                maxLines = maxLines,
            )
        }
    }
}

@Composable
private fun FreudHomeHeaderAvatar(
    avatarImageUrl: String?,
    avatarContentDescription: String?,
) {
    FreudAsyncImage(
        imageUrl = avatarImageUrl,
        contentDescription = avatarContentDescription,
        shape = FreudAsyncImageShape.Circle,
        modifier = Modifier.size(FreudHomeHeaderDefaults.avatarSize.value),
    )
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
            tint = FreudHomeHeaderTheme.colorScheme.dateLabelText,
        )
        FreudText(
            value = dateLabel,
            color = FreudHomeHeaderTheme.colorScheme.dateLabelText,
            typography = FreudHomeHeaderTheme.typography.textXsBold,
            align = TextAlign.Start,
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
                horizontal = ds.dimension.dp8.value,
                vertical = ds.dimension.dp4.value,
            ),
        horizontalArrangement = Arrangement.spacedBy(ds.dimension.dp4.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FreudIcon(
            resource = Res.drawable.ic_star_solid,
            contentDescription = null,
            size = FreudHomeHeaderDefaults.inlineIconSize,
            tint = FreudHomeHeaderTheme.colorScheme.mentorAccent,
        )
        FreudText(
            value = label,
            color = FreudHomeHeaderTheme.colorScheme.mentorAccent,
            typography = FreudHomeHeaderTheme.typography.textXsBold,
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

    val FreudColorScheme.dateLabelText: FreudDsToken<Color>
        @Composable get() = this provides FreudDynamicColor(
            light = FreudDsToken(
                super.color.brown100.value.copy(
                    alpha = FreudHomeHeaderDefaults.DATE_LABEL_TEXT_ALPHA_LIGHT,
                ),
            ),
            dark = FreudDsToken(
                super.color.brown40.value.copy(
                    alpha = FreudHomeHeaderDefaults.DATE_LABEL_TEXT_ALPHA_DARK,
                ),
            ),
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
