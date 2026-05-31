package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.kigya.headway.core.designSystem.component.FreudHomeHeader
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderContent
import dev.kigya.headway.core.designSystem.component.FreudHomeHeaderMetrics
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.preview.FreudHomeHeaderPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudHomeHeaderPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudHomeHeaderPreviewTheme.text
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.isWide

private object FreudHomeHeaderPreviewTheme : FreudTheme() {
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

private enum class FreudHomeHeaderPreviewVariant {
    FULL,
    NO_ROLE,
    NO_METRICS,
    MINIMAL,
}

private data class FreudHomeHeaderPreviewCase(
    val isDark: Boolean,
    val variant: FreudHomeHeaderPreviewVariant,
    val isAvatarEmpty: Boolean,
)

private class FreudHomeHeaderPreviewCaseProvider : PreviewParameterProvider<FreudHomeHeaderPreviewCase> {
    override val values: Sequence<FreudHomeHeaderPreviewCase> = sequence {
        val themes = listOf(false, true)
        val variants = FreudHomeHeaderPreviewVariant.entries
        val avatarFlags = listOf(false, true)
        for (isDark in themes) {
            for (variant in variants) {
                for (isAvatarEmpty in avatarFlags) {
                    yield(
                        FreudHomeHeaderPreviewCase(
                            isDark = isDark,
                            variant = variant,
                            isAvatarEmpty = isAvatarEmpty,
                        ),
                    )
                }
            }
        }
    }
}

private fun previewHomeHeaderContent(
    variant: FreudHomeHeaderPreviewVariant,
    isAvatarEmpty: Boolean,
): FreudHomeHeaderContent {
    val metrics = when (variant) {
        FreudHomeHeaderPreviewVariant.FULL,
        FreudHomeHeaderPreviewVariant.NO_ROLE,
        -> FreudHomeHeaderMetrics(
            readinessLine = FreudTextValue.text(PREVIEW_METRIC_READINESS_LINE),
            nextSessionLine = FreudTextValue.text(PREVIEW_METRIC_NEXT_SESSION_LINE),
        )

        FreudHomeHeaderPreviewVariant.NO_METRICS,
        FreudHomeHeaderPreviewVariant.MINIMAL,
        -> null
    }
    val role = when (variant) {
        FreudHomeHeaderPreviewVariant.FULL,
        FreudHomeHeaderPreviewVariant.NO_METRICS,
        -> FreudTextValue.text(PREVIEW_ROLE_LABEL)

        FreudHomeHeaderPreviewVariant.NO_ROLE,
        FreudHomeHeaderPreviewVariant.MINIMAL,
        -> null
    }
    return FreudHomeHeaderContent(
        greetingPrimary = FreudTextValue.text(PREVIEW_GREETING_PRIMARY_LINE),
        greetingShortIfOverflow = FreudTextValue.text(PREVIEW_GREETING_SHORT_LINE),
        dateLabel = FreudTextValue.text(PREVIEW_DATE_LINE),
        roleLabel = role,
        metrics = metrics,
        avatarImageUrl = if (isAvatarEmpty) null else SAMPLE_AVATAR_URL,
    )
}

@Preview(
    name = "FreudHomeHeader – narrow canvas",
    widthDp = PREVIEW_CANVAS_WIDTH_NARROW_DP,
    heightDp = PREVIEW_CANVAS_HEIGHT_DP,
    showBackground = false,
)
@Composable
private fun FreudHomeHeaderPreviewNarrow(
    @PreviewParameter(FreudHomeHeaderPreviewCaseProvider::class) case: FreudHomeHeaderPreviewCase,
) {
    FreudHomeHeaderPreviewContent(case = case)
}

@Preview(
    name = "FreudHomeHeader – wide canvas",
    widthDp = PREVIEW_CANVAS_WIDTH_WIDE_DP,
    heightDp = PREVIEW_CANVAS_HEIGHT_DP,
    showBackground = false,
)
@Composable
private fun FreudHomeHeaderPreviewWide(
    @PreviewParameter(FreudHomeHeaderPreviewCaseProvider::class) case: FreudHomeHeaderPreviewCase,
) {
    FreudHomeHeaderPreviewContent(case = case)
}

@Composable
private fun FreudHomeHeaderPreviewContent(case: FreudHomeHeaderPreviewCase) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme
        val label = buildString {
            append(if (case.isDark) PREVIEW_LABEL_DARK else PREVIEW_LABEL_LIGHT)
            append(PREVIEW_LABEL_SEPARATOR)
            append(case.variant.name)
            append(PREVIEW_LABEL_SEPARATOR)
            append(
                if (isWide()) {
                    PREVIEW_LAYOUT_WIDE_LABEL
                } else {
                    PREVIEW_LAYOUT_NARROW_LABEL
                },
            )
            append(PREVIEW_LABEL_SEPARATOR)
            append(
                if (case.isAvatarEmpty) {
                    PREVIEW_AVATAR_FALLBACK_LABEL
                } else {
                    PREVIEW_AVATAR_REMOTE_LABEL
                },
            )
        }
        Column(
            modifier = Modifier
                .background(FreudHomeHeaderPreviewTheme.colorScheme.surface.value)
                .padding(ds.dimension.dp16.value),
        ) {
            FreudText(
                value = FreudTextValue.text(label),
                color = FreudHomeHeaderPreviewTheme.colorScheme.text,
                typography = ds.typography.textSmSemiBold,
            )
            Column(
                modifier = Modifier
                    .padding(top = ds.dimension.dp12.value)
                    .background(FreudHomeHeaderPreviewTheme.colorScheme.block.value),
            ) {
                FreudHomeHeader(
                    content = previewHomeHeaderContent(
                        variant = case.variant,
                        isAvatarEmpty = case.isAvatarEmpty,
                    ),
                    avatarContentDescription = null,
                )
            }
        }
    }
}

private const val PREVIEW_LABEL_DARK = "Dark"

private const val PREVIEW_LABEL_LIGHT = "Light"

private const val PREVIEW_LABEL_SEPARATOR = " · "

private const val PREVIEW_GREETING_PRIMARY_LINE = "Hi, Maksim Verylong Displayname!"

private const val PREVIEW_GREETING_SHORT_LINE = "Hi, Maksim!"

private const val PREVIEW_DATE_LINE = "Tue, 25 Jan 2026"

private const val PREVIEW_ROLE_LABEL = "Mentor"

private const val PREVIEW_METRIC_READINESS_LINE = "100%"

private const val PREVIEW_METRIC_NEXT_SESSION_LINE = "Check"

private const val PREVIEW_AVATAR_FALLBACK_LABEL = "Fallback avatar"

private const val PREVIEW_AVATAR_REMOTE_LABEL = "Remote avatar"

private const val PREVIEW_LAYOUT_NARROW_LABEL = "Narrow"

private const val PREVIEW_LAYOUT_WIDE_LABEL = "Wide"

private const val PREVIEW_CANVAS_WIDTH_NARROW_DP = 360

private const val PREVIEW_CANVAS_WIDTH_WIDE_DP = 900

private const val PREVIEW_CANVAS_HEIGHT_DP = 800

private const val SAMPLE_AVATAR_URL = "https://picsum.photos/seed/headway-ds-header/128/128"
