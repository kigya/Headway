package dev.kigya.headway.feature.auth.internal.ui.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudButtonIconSpec
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudIcon
import dev.kigya.headway.core.designSystem.component.FreudImage
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.core.designSystem.util.colorizeSubStrings
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.authBackground
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.authUnderButtonTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.googleSingInButtonColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.googleSingInButtonTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.greetingHighlight
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.greetingSubTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.greetingTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.learnAsGuestButtonColor
import dev.kigya.headway.feature.auth.internal.ui.theme.AuthTheme.transparent
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.freud_auth_dark_theme_image
import headway.core.design_system.generated.resources.freud_auth_light_theme_image
import headway.core.design_system.generated.resources.freud_auth_under_button_text
import headway.core.design_system.generated.resources.freud_greeting_auth_subtext
import headway.core.design_system.generated.resources.freud_greeting_auth_text
import headway.core.design_system.generated.resources.freud_learn_as_guest_auth_button
import headway.core.design_system.generated.resources.freud_robot_dark_theme_img
import headway.core.design_system.generated.resources.freud_robot_light_theme_img
import headway.core.design_system.generated.resources.freud_sign_in_google_auth_button
import headway.core.design_system.generated.resources.freud_substring_for_colorized_auth_header
import headway.core.design_system.generated.resources.ic_freud_google
import headway.core.design_system.generated.resources.ic_freud_logo
import headway.core.design_system.generated.resources.ic_freud_warning
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val EXPANDED_WIDTH_THRESHOLD = 600.dp
private val ACTION_BUTTON_WIDTH_DESKTOP = 348.dp
private const val MOBILE_ILLUSTRATION_HEIGHT_RATIO = 0.8f

@Composable
internal fun AuthScreen() {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContent(
        state = state,
        onIntent = {},
    )
}

@Composable
private fun AuthScreenContent(
    state: State<AuthStore.State>,
    onIntent: (AuthStore.Intent) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = AuthTheme.colorScheme.authBackground,
                pattern = FreudBackgroundPattern.Waves,
            ),
    ) {
        val isExpanded = maxWidth >= EXPANDED_WIDTH_THRESHOLD
        if (isExpanded) {
            AuthScreenExpandedContent(onIntent = onIntent)
        } else {
            AuthScreenCompactContent(onIntent = onIntent)
        }
    }
}

@Composable
private fun AuthScreenCompactContent(onIntent: (AuthStore.Intent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AuthTheme.dimension.dp24.value),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FreudSpacer(size = AuthTheme.dimension.dp64)
        AuthLogo()
        FreudSpacer(size = AuthTheme.dimension.dp36)
        AuthHeaderTexts(
            textAlign = TextAlign.Center,
            titleTypography = AuthTheme.typography.headingSmExtraBold,
            subtitleTypography = AuthTheme.typography.textLgSemiBold,
        )
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            AuthIllustration(isExpanded = false)
        }
        AuthActionButtons(
            modifier = Modifier.fillMaxWidth(),
            buttonSize = FreudHorizontalButtonSize.LARGE,
        )
        FreudSpacer(size = AuthTheme.dimension.dp36)
    }
}

@Composable
private fun AuthScreenExpandedContent(onIntent: (AuthStore.Intent) -> Unit) {
    Row(modifier = Modifier.fillMaxSize()) {
        AuthIllustration(
            isExpanded = true,
            modifier = Modifier.fillMaxHeight(),
        )

        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxHeight()
                .padding(horizontal = AuthTheme.dimension.dp24.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthLogo()
            FreudSpacer(size = AuthTheme.dimension.dp36)
            AuthHeaderTexts(
                textAlign = TextAlign.Center,
                titleTypography = AuthTheme.typography.headingMdExtraBold,
                subtitleTypography = AuthTheme.typography.textXlSemiBold,
            )
            FreudSpacer(size = AuthTheme.dimension.dp48)
            AuthActionButtons(
                modifier = Modifier.width(width = ACTION_BUTTON_WIDTH_DESKTOP),
                buttonSize = FreudHorizontalButtonSize.LARGE,
            )
        }
    }
}

@Composable
private fun AuthLogo() {
    FreudIcon(
        resource = Res.drawable.ic_freud_logo,
        contentDescription = null,
        size = AuthTheme.dimension.dp64,
    )
}

@Composable
private fun AuthHeaderTexts(
    textAlign: TextAlign,
    titleTypography: FreudDsToken<TextStyle>,
    subtitleTypography: FreudDsToken<TextStyle>,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FreudText(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(resource = Res.string.freud_greeting_auth_text)
                .colorizeSubStrings(
                    subString = stringResource(resource = Res.string.freud_substring_for_colorized_auth_header),
                    color = AuthTheme.colorScheme.greetingHighlight,
                ),
            color = AuthTheme.colorScheme.greetingTextColor,
            typography = titleTypography,
            align = textAlign,
        )
        FreudSpacer(size = AuthTheme.dimension.dp24)
        FreudText(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(resource = Res.string.freud_greeting_auth_subtext),
            color = AuthTheme.colorScheme.greetingSubTextColor,
            typography = subtitleTypography,
            align = textAlign,
        )
    }
}

@Composable
private fun AuthIllustration(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val isDark = isSystemInDarkTheme()
    val resource = when {
        isExpanded && isDark -> Res.drawable.freud_auth_dark_theme_image
        isExpanded -> Res.drawable.freud_auth_light_theme_image
        isDark -> Res.drawable.freud_robot_dark_theme_img
        else -> Res.drawable.freud_robot_light_theme_img
    }

    val heightModifier = if (isExpanded) {
        modifier.fillMaxHeight()
    } else {
        modifier.fillMaxHeight(fraction = MOBILE_ILLUSTRATION_HEIGHT_RATIO)
    }

    val contentScale = if (isExpanded) {
        ContentScale.FillHeight
    } else {
        ContentScale.Fit
    }

    FreudImage(
        resource = resource,
        modifier = heightModifier,
        contentScale = contentScale,
    )
}

@Composable
private fun AuthActionButtons(
    buttonSize: FreudHorizontalButtonSize,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(resource = Res.string.freud_learn_as_guest_auth_button),
            onClick = {},
            containerColor = AuthTheme.colorScheme.transparent,
            contentColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            borderColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            size = buttonSize,
        )
        FreudSpacer(size = AuthTheme.dimension.dp24)
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(resource = Res.string.freud_sign_in_google_auth_button),
            onClick = {},
            containerColor = AuthTheme.colorScheme.googleSingInButtonColor,
            contentColor = AuthTheme.colorScheme.googleSingInButtonTextColor,
            size = buttonSize,
            leadingIcon = FreudButtonIconSpec.Static(
                resource = Res.drawable.ic_freud_google,
                tint = null,
            ),
            supportingText = stringResource(resource = Res.string.freud_auth_under_button_text),
            supportingColor = AuthTheme.colorScheme.authUnderButtonTextColor,
            supportingIcon = FreudButtonIconSpec.Static(
                resource = Res.drawable.ic_freud_warning,
                tint = AuthTheme.colorScheme.authUnderButtonTextColor,
            ),
        )
    }
}
