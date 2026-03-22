package dev.kigya.headway.core.designSystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import dev.kigya.headway.core.designSystem.component.FreudTopBarDefaults.contentColor
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.resolveToPlainStringOrNull
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.ic_chevron_left
import headway.core.design_system.generated.resources.ic_sign_out
import org.jetbrains.compose.resources.DrawableResource

private object FreudTopBarDefaults : FreudTheme() {
    val height = DefaultFreudTheme.dimension.dp48
    val slotSize = DefaultFreudTheme.dimension.dp48
    val iconSize = DefaultFreudTheme.dimension.dp24
    val contentSpacing = DefaultFreudTheme.dimension.dp12
    val borderWidth = DefaultFreudTheme.dimension.dp1

    val FreudColorScheme.contentColor: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown30,
        )
}

sealed interface FreudTopBarStartSlot {
    val onClick: () -> Unit
    val contentDescription: FreudTextValue?

    data class Back(
        override val onClick: () -> Unit,
        override val contentDescription: FreudTextValue? = null,
    ) : FreudTopBarStartSlot
}

sealed interface FreudTopBarEndSlot {
    val onClick: () -> Unit
    val contentDescription: FreudTextValue?

    data class SignOut(
        override val onClick: () -> Unit,
        override val contentDescription: FreudTextValue? = null,
    ) : FreudTopBarEndSlot
}

@Composable
fun FreudTopBar(
    modifier: Modifier = Modifier,
    title: FreudTextValue? = null,
    startSlot: FreudTopBarStartSlot? = null,
    endSlot: FreudTopBarEndSlot? = null,
    contentColor: FreudDsToken<Color> = FreudTopBarDefaults.colorScheme.contentColor,
    borderColor: FreudDsToken<Color> = contentColor,
    titleTypography: FreudDsToken<TextStyle> = FreudTheme.DefaultFreudTheme.typography.textXlExtraBold,
) {
    Row(
        modifier = modifier.height(FreudTopBarDefaults.height.value),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (startSlot != null) {
            FreudTopBarIconButton(
                resource = startSlot.toResource(),
                contentDescription = startSlot.contentDescription.resolveToPlainStringOrNull(),
                onClick = startSlot.onClick,
                tint = contentColor,
                borderColor = borderColor,
            )

            FreudSpacer(size = FreudTopBarDefaults.contentSpacing)
        }

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (title != null) {
                FreudText(
                    value = title,
                    color = contentColor,
                    typography = titleTypography,
                    align = TextAlign.Start,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        if (endSlot != null) {
            FreudSpacer(size = FreudTopBarDefaults.contentSpacing)

            FreudTopBarIconButton(
                resource = endSlot.toResource(),
                contentDescription = endSlot.contentDescription.resolveToPlainStringOrNull(),
                onClick = endSlot.onClick,
                tint = contentColor,
                borderColor = borderColor,
            )
        }
    }
}

@Composable
private fun FreudTopBarIconButton(
    resource: DrawableResource,
    contentDescription: String?,
    onClick: () -> Unit,
    tint: FreudDsToken<Color>,
    borderColor: FreudDsToken<Color>,
) {
    val shape: RoundedCornerShape = FreudTheme.DefaultFreudTheme.shape.circle.value

    Box(
        modifier = Modifier
            .size(FreudTopBarDefaults.slotSize.value)
            .clip(shape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .border(
                width = FreudTopBarDefaults.borderWidth.value,
                color = borderColor.value,
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        FreudIcon(
            resource = resource,
            contentDescription = contentDescription,
            size = FreudTopBarDefaults.iconSize,
            tint = tint,
        )
    }
}

private fun FreudTopBarStartSlot.toResource(): DrawableResource = when (this) {
    is FreudTopBarStartSlot.Back -> Res.drawable.ic_chevron_left
}

private fun FreudTopBarEndSlot.toResource(): DrawableResource = when (this) {
    is FreudTopBarEndSlot.SignOut -> Res.drawable.ic_sign_out
}
