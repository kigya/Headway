@file:Suppress("MagicNumber")

package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import org.jetbrains.compose.resources.DrawableResource

enum class FreudHorizontalButtonSize {
    LARGE,
    SMALL,
}

private object FreudButtonDefaults {
    val largeHeight = FreudTheme.DefaultFreudTheme.dimension.dp56
    val smallHeight = FreudTheme.DefaultFreudTheme.dimension.dp48

    val horizontalMinPadding = FreudTheme.DefaultFreudTheme.dimension.dp16
    val horizontalDefaultHorizontalPadding = FreudTheme.DefaultFreudTheme.dimension.dp24
    val horizontalFixedVerticalPadding = FreudTheme.DefaultFreudTheme.dimension.dp16

    val verticalFixedHorizontalPadding = FreudTheme.DefaultFreudTheme.dimension.dp16
    val verticalMinVerticalPadding = FreudTheme.DefaultFreudTheme.dimension.dp16

    val contentSpacing = FreudTheme.DefaultFreudTheme.dimension.dp8
    val supportingTopSpacing = FreudTheme.DefaultFreudTheme.dimension.dp8
    val supportingContentSpacing = FreudTheme.DefaultFreudTheme.dimension.dp4

    val borderWidth = FreudTheme.DefaultFreudTheme.dimension.dp1

    val iconSize = FreudTheme.DefaultFreudTheme.dimension.dp24
    val supportingIconSize = FreudTheme.DefaultFreudTheme.dimension.dp20
}

@Immutable
sealed interface FreudButtonIconSpec {
    val resource: DrawableResource
    val contentDescription: String?
    val tint: FreudDsToken<Color>?

    @Immutable
    data class Static(
        override val resource: DrawableResource,
        override val contentDescription: String? = null,
        override val tint: FreudDsToken<Color>? = null,
    ) : FreudButtonIconSpec

    @Immutable
    data class Animated(
        override val resource: DrawableResource,
        override val contentDescription: String? = null,
        override val tint: FreudDsToken<Color>? = null,
        val isVisible: Boolean = true,
        val animation: FreudAnimatedIconAnimation = FreudIconDefaults.fadeInScale(),
    ) : FreudButtonIconSpec
}

@Composable
fun FreudHorizontalButton(
    text: String,
    onClick: () -> Unit,
    containerColor: FreudDsToken<Color>,
    contentColor: FreudDsToken<Color>,
    size: FreudHorizontalButtonSize,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    borderColor: FreudDsToken<Color>? = null,
    borderWidth: FreudDsToken<Dp> = FreudButtonDefaults.borderWidth,
    horizontalPadding: FreudDsToken<Dp> = FreudButtonDefaults.horizontalDefaultHorizontalPadding,
    leadingIcon: FreudButtonIconSpec? = null,
    trailingIcon: FreudButtonIconSpec? = null,
    supportingText: String? = null,
    supportingColor: FreudDsToken<Color> = contentColor,
    supportingIcon: FreudButtonIconSpec? = null,
) {
    val shape: RoundedCornerShape = FreudTheme.DefaultFreudTheme.shape.rounding24.value

    val minHeight = when (size) {
        FreudHorizontalButtonSize.LARGE -> FreudButtonDefaults.largeHeight.value
        FreudHorizontalButtonSize.SMALL -> FreudButtonDefaults.smallHeight.value
    }

    val minPadding = FreudButtonDefaults.horizontalMinPadding.value
    val resolvedHorizontalPadding =
        if (horizontalPadding.value < minPadding) minPadding else horizontalPadding.value

    val resolvedVerticalPadding = FreudButtonDefaults.horizontalFixedVerticalPadding.value

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = minHeight) // <-- ключевая правка вместо .height(minHeight)
                .clip(shape)
                .background(containerColor.value)
                .then(
                    if (borderColor != null) {
                        Modifier.border(
                            width = borderWidth.value,
                            color = borderColor.value,
                            shape = shape,
                        )
                    } else {
                        Modifier
                    }
                )
                .clickable(
                    enabled = isEnabled,
                    role = Role.Button,
                    onClick = onClick,
                )
                .padding(
                    horizontal = resolvedHorizontalPadding,
                    vertical = resolvedVerticalPadding,
                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                FreudButtonIcon(icon = leadingIcon, size = FreudButtonDefaults.iconSize)
                FreudSpacer(size = FreudButtonDefaults.contentSpacing)
            }

            FreudText(
                value = text,
                color = contentColor,
                typography = FreudTheme.DefaultFreudTheme.typography.textLgExtraBold,
                maxLines = 1,
            )

            if (trailingIcon != null) {
                FreudSpacer(size = FreudButtonDefaults.contentSpacing)
                FreudButtonIcon(icon = trailingIcon, size = FreudButtonDefaults.iconSize)
            }
        }

        if (supportingText != null || supportingIcon != null) {
            FreudSpacer(size = FreudButtonDefaults.supportingTopSpacing)

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                if (supportingIcon != null) {
                    FreudButtonIcon(icon = supportingIcon, size = FreudButtonDefaults.supportingIconSize)
                    if (supportingText != null) {
                        FreudSpacer(size = FreudButtonDefaults.supportingContentSpacing)
                    }
                }

                if (supportingText != null) {
                    FreudText(
                        value = supportingText,
                        color = supportingColor,
                        typography = FreudTheme.DefaultFreudTheme.typography.textXsExtraBold,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

@Composable
fun FreudVerticalButton(
    text: String,
    onClick: () -> Unit,
    containerColor: FreudDsToken<Color>,
    contentColor: FreudDsToken<Color>,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    borderColor: FreudDsToken<Color>? = null,
    borderWidth: FreudDsToken<Dp> = FreudButtonDefaults.borderWidth,
    verticalPadding: FreudDsToken<Dp> = FreudButtonDefaults.verticalMinVerticalPadding,
    icon: FreudButtonIconSpec? = null,
) {
    val shape: RoundedCornerShape = FreudTheme.DefaultFreudTheme.shape.rounding24.value

    val minVp = FreudButtonDefaults.verticalMinVerticalPadding.value
    val resolvedVerticalPadding = if (verticalPadding.value < minVp) minVp else verticalPadding.value

    Column(
        modifier = modifier
            .clip(shape)
            .background(containerColor.value)
            .then(
                if (borderColor != null) {
                    Modifier.border(
                        width = borderWidth.value,
                        color = borderColor.value,
                        shape = shape,
                    )
                } else {
                    Modifier
                }
            )
            .clickable(
                enabled = isEnabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(
                horizontal = FreudButtonDefaults.verticalFixedHorizontalPadding.value,
                vertical = resolvedVerticalPadding,
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FreudText(
            value = text,
            color = contentColor,
            typography = FreudTheme.DefaultFreudTheme.typography.textLgExtraBold,
            maxLines = 1,
        )

        if (icon != null) {
            FreudSpacer(size = FreudButtonDefaults.contentSpacing)
            FreudButtonIcon(icon = icon, size = FreudButtonDefaults.iconSize)
        }
    }
}

@Composable
private fun FreudButtonIcon(
    icon: FreudButtonIconSpec,
    size: FreudDsToken<Dp>,
) {
    when (icon) {
        is FreudButtonIconSpec.Static -> FreudIcon(
            resource = icon.resource,
            contentDescription = icon.contentDescription,
            size = size,
            tint = icon.tint,
        )

        is FreudButtonIconSpec.Animated -> FreudAnimatedIcon(
            resource = icon.resource,
            contentDescription = icon.contentDescription,
            isVisible = icon.isVisible,
            size = size,
            tint = icon.tint,
            animation = icon.animation,
        )
    }
}
