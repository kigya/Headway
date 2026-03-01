package dev.kigya.headway.core.designSystem.component.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.kigya.headway.core.designSystem.component.FreudButtonIconSpec
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudIconDefaults
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.component.FreudVerticalButton
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.block
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.container
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.content
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.supporting
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.surface
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.text
import dev.kigya.headway.core.designSystem.component.preview.FreudButtonPreviewTheme.tint
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_el_baion
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource

private object FreudButtonPreviewTheme : FreudTheme() {

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

    // button colors (as in your screenshot)
    val FreudColorScheme.container
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown60,
        )

    val FreudColorScheme.content
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown10,
        )

    val FreudColorScheme.supporting
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.orange60,
            dark = super.color.orange40,
        )

    val FreudColorScheme.tint
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown10,
        )
}

private enum class FreudButtonPreviewKind {
    HORIZONTAL_LARGE,
    HORIZONTAL_SMALL,
    VERTICAL,
}

private enum class FreudButtonPreviewIconKind {
    NONE,
    STATIC,
    ANIM_FADE,
    ANIM_SCALE,
    ANIM_FADE_SCALE;

    val isAnimated: Boolean
        get() = this == ANIM_FADE || this == ANIM_SCALE || this == ANIM_FADE_SCALE
}

private data class FreudButtonPreviewCase(
    val isDark: Boolean,
    val kind: FreudButtonPreviewKind,
    val enabled: Boolean,
    val borderEnabled: Boolean,
    val leading: FreudButtonPreviewIconKind,
    val trailing: FreudButtonPreviewIconKind,
    val supportingEnabled: Boolean,
)

private class FreudButtonPreviewCaseProvider : PreviewParameterProvider<FreudButtonPreviewCase> {
    override val values: Sequence<FreudButtonPreviewCase> = sequence {
        val themes = listOf(false, true)

        val baseCases = listOf(
            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.HORIZONTAL_LARGE,
                enabled = true,
                borderEnabled = false,
                leading = FreudButtonPreviewIconKind.STATIC,
                trailing = FreudButtonPreviewIconKind.NONE,
                supportingEnabled = false,
            ),
            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.HORIZONTAL_LARGE,
                enabled = true,
                borderEnabled = true,
                leading = FreudButtonPreviewIconKind.ANIM_FADE,
                trailing = FreudButtonPreviewIconKind.STATIC,
                supportingEnabled = true,
            ),

            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.HORIZONTAL_SMALL,
                enabled = true,
                borderEnabled = false,
                leading = FreudButtonPreviewIconKind.ANIM_SCALE,
                trailing = FreudButtonPreviewIconKind.NONE,
                supportingEnabled = false,
            ),
            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.HORIZONTAL_SMALL,
                enabled = false,
                borderEnabled = true,
                leading = FreudButtonPreviewIconKind.STATIC,
                trailing = FreudButtonPreviewIconKind.ANIM_FADE_SCALE,
                supportingEnabled = true,
            ),

            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.VERTICAL,
                enabled = true,
                borderEnabled = false,
                leading = FreudButtonPreviewIconKind.ANIM_FADE_SCALE,
                trailing = FreudButtonPreviewIconKind.NONE,
                supportingEnabled = false,
            ),
            FreudButtonPreviewCase(
                isDark = false,
                kind = FreudButtonPreviewKind.VERTICAL,
                enabled = false,
                borderEnabled = true,
                leading = FreudButtonPreviewIconKind.STATIC,
                trailing = FreudButtonPreviewIconKind.NONE,
                supportingEnabled = false,
            ),
        )

        for (isDark in themes) {
            for (c in baseCases) {
                yield(c.copy(isDark = isDark))
            }
        }
    }
}

@Preview(
    name = "FreudButton – Theme x Variant",
    showBackground = false,
)
@Composable
@Suppress("LongMethod")
private fun FreudButtonPreview(
    @PreviewParameter(FreudButtonPreviewCaseProvider::class) case: FreudButtonPreviewCase,
) {
    FreudTheme(isDark = case.isDark) {
        val ds = FreudTheme.DefaultFreudTheme

        val surface = FreudButtonPreviewTheme.colorScheme.surface.value
        val block = FreudButtonPreviewTheme.colorScheme.block.value
        val text = FreudButtonPreviewTheme.colorScheme.text

        val container = FreudButtonPreviewTheme.colorScheme.container
        val content = FreudButtonPreviewTheme.colorScheme.content
        val supporting = FreudButtonPreviewTheme.colorScheme.supporting
        val border = if (case.borderEnabled) FreudButtonPreviewTheme.colorScheme.tint else null
        val iconTint = FreudButtonPreviewTheme.colorScheme.tint

        val needsAnimation = case.leading.isAnimated || case.trailing.isAnimated

        var visible by remember { mutableStateOf(true) }
        if (needsAnimation) {
            LaunchedEffect(case.kind, case.leading, case.trailing) {
                while (true) {
                    delay(ANIM_DELAY_MS)
                    visible = !visible
                }
            }
        }

        val themeLabel = if (case.isDark) "Dark" else "Light"
        val kindLabel = when (case.kind) {
            FreudButtonPreviewKind.HORIZONTAL_LARGE -> "Horizontal • Large"
            FreudButtonPreviewKind.HORIZONTAL_SMALL -> "Horizontal • Small"
            FreudButtonPreviewKind.VERTICAL -> "Vertical"
        }

        val header = buildString {
            append("$themeLabel • $kindLabel • ")
            append(if (case.enabled) "enabled" else "disabled")
            append(" • ")
            append(if (case.borderEnabled) "border=on" else "border=off")
            append("\n")
            append("leading=${case.leading.name} • trailing=${case.trailing.name} • ")
            append(if (case.supportingEnabled) "supporting=on" else "supporting=off")
        }

        val stageShape: RoundedCornerShape = ds.shape.rounding24.value

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
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
            )

            FreudSpacer(size = ds.dimension.dp12)

            when (case.kind) {
                FreudButtonPreviewKind.HORIZONTAL_LARGE -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(block, stageShape)
                            .padding(ds.dimension.dp16.value),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FreudHorizontalButton(
                            text = "Sign in",
                            onClick = {},
                            containerColor = container,
                            contentColor = content,
                            size = FreudHorizontalButtonSize.LARGE,
                            isEnabled = case.enabled,
                            borderColor = border,
                            leadingIcon = iconSpec(kind = case.leading, visible = visible, tint = iconTint),
                            trailingIcon = iconSpec(kind = case.trailing, visible = visible, tint = iconTint),
                            supportingText = if (case.supportingEnabled) "innowise.com only" else null,
                            supportingColor = supporting,
                            supportingIcon = if (case.supportingEnabled) {
                                iconSpec(
                                    kind = FreudButtonPreviewIconKind.STATIC,
                                    visible = true,
                                    tint = supporting,
                                )
                            } else {
                                null
                            },
                        )
                    }
                }

                FreudButtonPreviewKind.HORIZONTAL_SMALL -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(block, stageShape)
                            .padding(ds.dimension.dp16.value),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        FreudHorizontalButton(
                            text = "Continue",
                            onClick = {},
                            containerColor = container,
                            contentColor = content,
                            size = FreudHorizontalButtonSize.SMALL,
                            isEnabled = case.enabled,
                            borderColor = border,
                            leadingIcon = iconSpec(kind = case.leading, visible = visible, tint = iconTint),
                            trailingIcon = iconSpec(kind = case.trailing, visible = visible, tint = iconTint),
                            supportingText = if (case.supportingEnabled) "Secondary action" else null,
                            supportingColor = supporting,
                            supportingIcon = if (case.supportingEnabled) {
                                iconSpec(
                                    kind = FreudButtonPreviewIconKind.STATIC,
                                    visible = true,
                                    tint = supporting,
                                )
                            } else {
                                null
                            },
                        )
                    }
                }

                FreudButtonPreviewKind.VERTICAL -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(
                                    min = ds.dimension.dp128.value,
                                    max = ds.dimension.dp240.value,
                                )
                                .background(block, stageShape)
                                .padding(ds.dimension.dp16.value),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                FreudVerticalButton(
                                    text = "Next",
                                    onClick = {},
                                    containerColor = container,
                                    contentColor = content,
                                    isEnabled = case.enabled,
                                    borderColor = border,
                                    icon = iconSpec(kind = case.leading, visible = visible, tint = iconTint),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun iconSpec(
    kind: FreudButtonPreviewIconKind,
    visible: Boolean,
    tint: FreudDsToken<Color>,
): FreudButtonIconSpec? = when (kind) {
    FreudButtonPreviewIconKind.NONE -> null

    FreudButtonPreviewIconKind.STATIC -> FreudButtonIconSpec.Static(
        resource = PreviewButtonIconRes,
        contentDescription = null,
        tint = tint,
    )

    FreudButtonPreviewIconKind.ANIM_FADE -> FreudButtonIconSpec.Animated(
        resource = PreviewButtonIconRes,
        contentDescription = null,
        tint = tint,
        isVisible = visible,
        animation = FreudIconDefaults.fadeIn(),
    )

    FreudButtonPreviewIconKind.ANIM_SCALE -> FreudButtonIconSpec.Animated(
        resource = PreviewButtonIconRes,
        contentDescription = null,
        tint = tint,
        isVisible = visible,
        animation = FreudIconDefaults.scale(),
    )

    FreudButtonPreviewIconKind.ANIM_FADE_SCALE -> FreudButtonIconSpec.Animated(
        resource = PreviewButtonIconRes,
        contentDescription = null,
        tint = tint,
        isVisible = visible,
        animation = FreudIconDefaults.fadeInScale(),
    )
}

private const val ANIM_DELAY_MS = 900L
private val PreviewButtonIconRes: DrawableResource = Res.drawable.ic_el_baion
