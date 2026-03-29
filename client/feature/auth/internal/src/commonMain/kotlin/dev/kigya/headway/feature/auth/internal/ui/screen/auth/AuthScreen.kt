package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudButtonIconSpec
import dev.kigya.headway.core.designSystem.component.FreudFallback
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudIcon
import dev.kigya.headway.core.designSystem.component.FreudLottie
import dev.kigya.headway.core.designSystem.component.FreudLottieSource
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.FreudScreenByWidth
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.authBackground
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.authUnderButtonTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.googleSingInButtonColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.googleSingInButtonTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.greetingHighlight
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.greetingSubTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.greetingTextColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.learnAsGuestButtonColor
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.transparent
import headway.feature.auth.internal.generated.resources.Res
import headway.feature.auth.internal.generated.resources.auth_greeting_subtext
import headway.feature.auth.internal.generated.resources.auth_greeting_text
import headway.feature.auth.internal.generated.resources.auth_learn_as_guest_button
import headway.feature.auth.internal.generated.resources.auth_sing_in_google_button
import headway.feature.auth.internal.generated.resources.auth_substring_for_colorized_header
import headway.feature.auth.internal.generated.resources.auth_under_button_text
import headway.feature.auth.internal.generated.resources.ic_google
import headway.feature.auth.internal.generated.resources.ic_headway_logo
import headway.feature.auth.internal.generated.resources.ic_warning
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun AuthScreen() {
    val viewModel = koinViewModel<AuthViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    AuthScreenContent(
        state = state,
        onOpenNoAccess = viewModel::onOpenNoAccess,
    )
}

@Composable
private fun AuthScreenContent(
    state: State<AuthStore.State>,
    onOpenNoAccess: () -> Unit,
) {
    FreudScreenByWidth(
        modifier = Modifier.background(
            color = AuthTheme.colorScheme.authBackground,
            pattern = FreudBackgroundPattern.Waves,
        ),
        narrow = {
            FreudFallback(
                isError = false,
                onRetry = { },
            ) {
                AuthScreenNarrowContent()
            }
        },
        wide = {
            FreudFallback(
                isError = false,
                onRetry = { },
            ) {
                AuthScreenWideContent()
            }
        },
    )
}

@Composable
private fun AuthScreenNarrowContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = AuthTheme.dimension.dp24.value),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FreudSpacer(size = AuthTheme.dimension.dp64)
        AuthLogo()
        FreudSpacer(size = AuthTheme.dimension.dp36)
        AuthHeaderTexts(isWide = false)
        Box(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            FreudLottie(
                reader = { Res.readBytes("files/lottie_auth_narrow.lottie") },
                source = FreudLottieSource.DotLottie,
                modifier = Modifier
                    .fillMaxWidth(fraction = 1f)
                    .aspectRatio(ratio = LOTTIE_ASPECT_RATIO),
            )
        }
        AuthActionButtons(modifier = Modifier.fillMaxWidth())
        FreudSpacer(size = AuthTheme.dimension.dp36)
    }
}

@Composable
private fun AuthScreenWideContent() {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(max = WIDE_LOTTIE_MAX_WIDTH)
                .fillMaxHeight(),
            contentAlignment = Alignment.BottomStart,
        ) {
            FreudLottie(
                reader = { Res.readBytes("files/lottie_auth_wide.lottie") },
                source = FreudLottieSource.DotLottie,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.FillHeight,
            )
        }

        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = AuthTheme.dimension.dp24.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthLogo()
            FreudSpacer(size = AuthTheme.dimension.dp36)
            AuthHeaderTexts(isWide = true)
            FreudSpacer(size = AuthTheme.dimension.dp48)
            AuthActionButtons(modifier = Modifier.width(width = ACTION_BUTTON_WIDTH_WIDE))
        }
    }
}

@Composable
private fun AuthLogo() {
    FreudIcon(
        resource = Res.drawable.ic_headway_logo,
        contentDescription = null,
        size = AuthTheme.dimension.dp64,
    )
}

@Composable
private fun AuthHeaderTexts(isWide: Boolean) {
    val titleTypography = if (isWide) {
        AuthTheme.typography.headingXlExtraBold
    } else {
        AuthTheme.typography.headingSmExtraBold
    }

    val subtitleTypography = if (isWide) {
        AuthTheme.typography.paragraphXl
    } else {
        AuthTheme.typography.paragraphLg
    }

    val highlightColor = AuthTheme.colorScheme.greetingHighlight

    Column(
        modifier = if (isWide) Modifier.fillMaxWidth() else Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FreudText(
            value = FreudTextValue.rich {
                append(resource = Res.string.auth_greeting_text)
                colored(
                    resource = Res.string.auth_substring_for_colorized_header,
                    color = highlightColor,
                )
            },
            color = AuthTheme.colorScheme.greetingTextColor,
            typography = titleTypography,
            align = TextAlign.Center,
        )
        FreudSpacer(size = AuthTheme.dimension.dp24)
        FreudText(
            value = FreudTextValue.text(resource = Res.string.auth_greeting_subtext),
            color = AuthTheme.colorScheme.greetingSubTextColor,
            typography = subtitleTypography,
            align = TextAlign.Center,
        )
    }
}

@Composable
private fun AuthActionButtons(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = FreudTextValue.text(resource = Res.string.auth_learn_as_guest_button),
            onClick = { },
            containerColor = AuthTheme.colorScheme.transparent,
            contentColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            borderColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            size = FreudHorizontalButtonSize.LARGE,
        )
        FreudSpacer(size = AuthTheme.dimension.dp20)
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = FreudTextValue.text(resource = Res.string.auth_sing_in_google_button),
            onClick = { },
            containerColor = AuthTheme.colorScheme.googleSingInButtonColor,
            contentColor = AuthTheme.colorScheme.googleSingInButtonTextColor,
            size = FreudHorizontalButtonSize.LARGE,
            leadingIcon = FreudButtonIconSpec.Static(
                resource = Res.drawable.ic_google,
                tint = null,
            ),
            supportingText = FreudTextValue.text(resource = Res.string.auth_under_button_text),
            supportingColor = AuthTheme.colorScheme.authUnderButtonTextColor,
            supportingIcon = FreudButtonIconSpec.Static(
                resource = Res.drawable.ic_warning,
                tint = AuthTheme.colorScheme.authUnderButtonTextColor,
            ),
        )
    }
}

private val ACTION_BUTTON_WIDTH_WIDE = 348.dp
private const val LOTTIE_ASPECT_RATIO = 1f
private val WIDE_LOTTIE_MAX_WIDTH = 520.dp
