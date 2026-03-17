package dev.kigya.headway.feature.auth.internal.ui.screen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
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
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.freud_auth_under_button_text
import headway.core.design_system.generated.resources.freud_greeting_auth_subtext
import headway.core.design_system.generated.resources.freud_greeting_auth_text
import headway.core.design_system.generated.resources.freud_learn_as_guest_auth_button
import headway.core.design_system.generated.resources.freud_robot_dark_theme_img
import headway.core.design_system.generated.resources.freud_robot_light_theme_img
import headway.core.design_system.generated.resources.freud_sing_in_google_auth_button
import headway.core.design_system.generated.resources.freud_substring_for_colorized_auth_header
import headway.core.design_system.generated.resources.ic_freud_google
import headway.core.design_system.generated.resources.ic_freud_logo
import headway.core.design_system.generated.resources.ic_freud_warning
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthScreen() {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContent(state)
}

@Composable
private fun AuthScreenContent(state: State<AuthStore.State>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = AuthTheme.colorScheme.authBackground,
                pattern = FreudBackgroundPattern.Waves,
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AuthTheme.dimension.dp24.value),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            FreudSpacer(size = AuthTheme.dimension.dp64)

            FreudIcon(
                resource = Res.drawable.ic_freud_logo,
                contentDescription = null,
                size = AuthTheme.dimension.dp64,
            )

            FreudSpacer(size = AuthTheme.dimension.dp36)

            FreudText(
                modifier = Modifier.fillMaxWidth(),
                value = stringResource(Res.string.freud_greeting_auth_text)
                    .colorizeSubStrings(
                        subString = stringResource(Res.string.freud_substring_for_colorized_auth_header),
                        color = AuthTheme.colorScheme.greetingHighlight
                    ),
                color = AuthTheme.colorScheme.greetingTextColor,
                typography = AuthTheme.typography.headingSmExtraBold,
                align = TextAlign.Center,
            )

            FreudSpacer(size = AuthTheme.dimension.dp24)

            FreudText(
                modifier = Modifier.fillMaxWidth(),
                value = stringResource(Res.string.freud_greeting_auth_subtext),
                color = AuthTheme.colorScheme.greetingSubTextColor,
                typography = AuthTheme.typography.textLgSemiBold,
                align = TextAlign.Center,
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                FreudImage(
                    resource = if (isSystemInDarkTheme()) Res.drawable.freud_robot_dark_theme_img
                    else Res.drawable.freud_robot_light_theme_img,
                    modifier = Modifier.fillMaxHeight(0.8f),
                    contentScale = ContentScale.Fit,
                )
            }

            FreudHorizontalButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.freud_learn_as_guest_auth_button),
                onClick = { /* TODO */ },
                containerColor = FreudDsToken(androidx.compose.ui.graphics.Color.Transparent),
                contentColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
                borderColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
                size = FreudHorizontalButtonSize.LARGE,
            )

            FreudSpacer(size = AuthTheme.dimension.dp24)

            FreudHorizontalButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(Res.string.freud_sing_in_google_auth_button),
                onClick = { /* TODO */ },
                containerColor = AuthTheme.colorScheme.googleSingInButtonColor,
                contentColor = AuthTheme.colorScheme.googleSingInButtonTextColor,
                size = FreudHorizontalButtonSize.LARGE,
                leadingIcon = FreudButtonIconSpec.Static(
                    resource = Res.drawable.ic_freud_google,
                    tint = null,
                ),
                supportingText = stringResource(Res.string.freud_auth_under_button_text),
                supportingColor = AuthTheme.colorScheme.authUnderButtonTextColor,
                supportingIcon = FreudButtonIconSpec.Static(
                    resource = Res.drawable.ic_freud_warning,
                    tint = AuthTheme.colorScheme.authUnderButtonTextColor,
                )
            )

            FreudSpacer(size = AuthTheme.dimension.dp36)
        }
    }
}
