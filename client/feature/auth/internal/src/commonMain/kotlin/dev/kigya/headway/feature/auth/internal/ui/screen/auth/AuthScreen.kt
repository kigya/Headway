package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
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
import dev.kigya.headway.core.designSystem.util.FreudAnimationTrigger
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.FreudTextAnimation
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.core.designSystem.util.rememberWindowSizeClass
import dev.kigya.headway.feature.auth.internal.ui.layout.AuthSideBySideLottieColumn
import dev.kigya.headway.feature.auth.internal.ui.layout.authSideBySideActionButtonWidth
import dev.kigya.headway.feature.auth.internal.ui.layout.authSideBySideMinTextColumnWidth
import dev.kigya.headway.feature.auth.internal.ui.layout.resolveAuthSideBySideLottieColumnWidth
import dev.kigya.headway.feature.auth.internal.ui.layout.shouldUseAuthSideBySideLayout
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
        onSignInWithGoogle = viewModel::onSignInWithGoogle,
        onContinueAsGuest = viewModel::onContinueAsGuest,
        onDismissError = viewModel::onDismissError,
    )
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun AuthScreenContent(
    state: State<AuthStore.State>,
    onSignInWithGoogle: () -> Unit,
    onContinueAsGuest: () -> Unit,
    onDismissError: () -> Unit,
) {
    val windowSizeClass = rememberWindowSizeClass()
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = AuthTheme.colorScheme.authBackground,
                pattern = FreudBackgroundPattern.Waves,
            ),
    ) {
        val horizontalPaddingWide = AuthTheme.dimension.dp24.value * 2
        val lottieSideBudget = resolveAuthSideBySideLottieColumnWidth(maxWidth = maxWidth)
        val useSideBySide = shouldUseAuthSideBySideLayout(
            containerWidth = maxWidth,
            widthSizeClass = windowSizeClass.widthSizeClass,
            lottieSideBudget = lottieSideBudget,
            textColumnHorizontalPadding = horizontalPaddingWide,
        )
        FreudFallback(
            isError = state.value.hasError,
            onRetry = onDismissError,
        ) {
            if (useSideBySide) {
                AuthScreenWideContent(
                    lottieColumnWidth = lottieSideBudget,
                    isBusy = state.value.isBusy,
                    onSignInWithGoogle = onSignInWithGoogle,
                    onContinueAsGuest = onContinueAsGuest,
                )
            } else {
                AuthScreenStackedContent(
                    containerWidth = maxWidth,
                    containerHeight = maxHeight,
                    isBusy = state.value.isBusy,
                    onSignInWithGoogle = onSignInWithGoogle,
                    onContinueAsGuest = onContinueAsGuest,
                )
            }
        }
    }
}

@Composable
private fun AuthScreenStackedContent(
    containerWidth: Dp,
    containerHeight: Dp,
    isBusy: Boolean,
    onSignInWithGoogle: () -> Unit,
    onContinueAsGuest: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val stackedLottieHeight = resolveStackedLottieHeight(
        containerWidth = containerWidth,
        containerHeight = containerHeight,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(horizontal = AuthTheme.dimension.dp24.value),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FreudSpacer(size = AuthTheme.dimension.dp64)
        AuthLogo()
        FreudSpacer(size = AuthTheme.dimension.dp36)
        AuthHeaderTexts(isWide = false)
        if (stackedLottieHeight != null) {
            FreudSpacer(size = AuthTheme.dimension.dp24)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = stackedLottieHeight)
                    .clip(shape = RectangleShape),
                contentAlignment = Alignment.Center,
            ) {
                FreudLottie(
                    reader = { Res.readBytes("files/lottie_auth_narrow.lottie") },
                    source = FreudLottieSource.DotLottie,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit,
                )
            }
        }
        FreudSpacer(size = AuthTheme.dimension.dp36)
        AuthActionButtons(
            isBusy = isBusy,
            onSignInWithGoogle = onSignInWithGoogle,
            onContinueAsGuest = onContinueAsGuest,
            modifier = Modifier
                .widthIn(max = authSideBySideActionButtonWidth)
                .fillMaxWidth(),
        )
        FreudSpacer(size = AuthTheme.dimension.dp36)
    }
}

@Composable
private fun AuthScreenWideContent(
    lottieColumnWidth: Dp,
    isBusy: Boolean,
    onSignInWithGoogle: () -> Unit,
    onContinueAsGuest: () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AuthSideBySideLottieColumn(lottieColumnWidth = lottieColumnWidth) {
            FreudLottie(
                reader = { Res.readBytes("files/lottie_auth_wide.lottie") },
                source = FreudLottieSource.DotLottie,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                alignment = Alignment.BottomStart,
                contentScale = ContentScale.FillHeight,
            )
        }

        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .widthIn(min = authSideBySideMinTextColumnWidth)
                .fillMaxHeight()
                .padding(horizontal = AuthTheme.dimension.dp24.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AuthLogo()
            FreudSpacer(size = AuthTheme.dimension.dp36)
            AuthHeaderTexts(isWide = true)
            FreudSpacer(size = AuthTheme.dimension.dp48)
            AuthActionButtons(
                isBusy = isBusy,
                onSignInWithGoogle = onSignInWithGoogle,
                onContinueAsGuest = onContinueAsGuest,
                modifier = Modifier.width(width = authSideBySideActionButtonWidth),
            )
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
    val titleFinishedTrigger = remember { FreudAnimationTrigger() }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        FreudText(
            value = FreudTextValue.rich {
                append(resource = Res.string.auth_greeting_text)
                colored(
                    resource = Res.string.auth_substring_for_colorized_header,
                    color = highlightColor,
                )
                animate(
                    animation = FreudTextAnimation.FadeIn(GREETING_ANIMATION_DURATION),
                    onFinish = titleFinishedTrigger,
                )
            },
            color = AuthTheme.colorScheme.greetingTextColor,
            typography = titleTypography,
            align = TextAlign.Center,
        )

        FreudSpacer(size = AuthTheme.dimension.dp24)

        FreudText(
            value = FreudTextValue.text(
                resource = Res.string.auth_greeting_subtext,
                animation = FreudTextAnimation.SlideIn(
                    startTrigger = titleFinishedTrigger,
                    durationMs = SUBTEXT_GREETING_ANIMATION_DURATION,
                ),
            ),
            color = AuthTheme.colorScheme.greetingSubTextColor,
            typography = subtitleTypography,
            align = TextAlign.Center,
        )
    }
}

@Composable
private fun AuthActionButtons(
    isBusy: Boolean,
    onSignInWithGoogle: () -> Unit,
    onContinueAsGuest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = FreudTextValue.text(resource = Res.string.auth_learn_as_guest_button),
            onClick = onContinueAsGuest,
            containerColor = AuthTheme.colorScheme.transparent,
            contentColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            borderColor = AuthTheme.colorScheme.learnAsGuestButtonColor,
            size = FreudHorizontalButtonSize.LARGE,
            isEnabled = !isBusy,
        )
        FreudSpacer(size = AuthTheme.dimension.dp20)
        FreudHorizontalButton(
            modifier = Modifier.fillMaxWidth(),
            text = FreudTextValue.text(resource = Res.string.auth_sing_in_google_button),
            onClick = onSignInWithGoogle,
            containerColor = AuthTheme.colorScheme.googleSingInButtonColor,
            contentColor = AuthTheme.colorScheme.googleSingInButtonTextColor,
            size = FreudHorizontalButtonSize.LARGE,
            isEnabled = !isBusy,
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

private fun resolveStackedLottieHeight(
    containerWidth: Dp,
    containerHeight: Dp,
): Dp? {
    val contentWidth = containerWidth - AuthTheme.dimension.dp24.value * 2
    if (contentWidth < STACKED_LOTTIE_MIN_SIZE) {
        return null
    }

    val availableHeight = containerHeight - STACKED_LOTTIE_FIXED_SIBLINGS_HEIGHT
    if (availableHeight < STACKED_LOTTIE_MIN_SIZE) {
        return null
    }

    val cappedHeight = minOf(
        STACKED_LOTTIE_MAX_HEIGHT_CAP,
        containerWidth * STACKED_LOTTIE_MAX_HEIGHT_WIDTH_FACTOR,
        availableHeight,
    )
    if (cappedHeight < STACKED_LOTTIE_MIN_SIZE || contentWidth < STACKED_LOTTIE_MIN_SIZE) {
        return null
    }

    return cappedHeight
}

private val STACKED_LOTTIE_MIN_SIZE = 120.dp
private val STACKED_LOTTIE_MAX_HEIGHT_CAP = 260.dp
private val STACKED_LOTTIE_FIXED_SIBLINGS_HEIGHT = 540.dp
private const val STACKED_LOTTIE_MAX_HEIGHT_WIDTH_FACTOR = 0.65f
private const val GREETING_ANIMATION_DURATION = 1000
private const val SUBTEXT_GREETING_ANIMATION_DURATION = 600
