@file:Suppress("MagicNumber", "LongMethod")

package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.component.FreudFallbackTheme.background
import dev.kigya.headway.core.designSystem.component.FreudFallbackTheme.body
import dev.kigya.headway.core.designSystem.component.FreudFallbackTheme.buttonContainer
import dev.kigya.headway.core.designSystem.component.FreudFallbackTheme.buttonContent
import dev.kigya.headway.core.designSystem.component.FreudFallbackTheme.title
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import dev.kigya.headway.core.designSystem.theme.FreudTheme
import dev.kigya.headway.core.designSystem.theme.color.FreudColorScheme
import dev.kigya.headway.core.designSystem.theme.color.FreudDynamicColor
import dev.kigya.headway.core.designSystem.theme.color.provides
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.core.designSystem.util.isWide
import headway.core.design_system.generated.resources.Res
import headway.core.design_system.generated.resources.freud_fallback_error_body
import headway.core.design_system.generated.resources.freud_fallback_error_title
import headway.core.design_system.generated.resources.freud_fallback_network_body
import headway.core.design_system.generated.resources.freud_fallback_network_title
import headway.core.design_system.generated.resources.freud_fallback_retry_button
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource

private enum class FreudFallbackStubKind {
    Error,
    Network,
}

private object FreudFallbackDefaults {
    val lottieSize = FreudDsToken(248.dp)
    val contentMaxWidth = FreudDsToken(920.dp)
    val textMaxWidth = FreudDsToken(320.dp)
    val buttonWidth = FreudTheme.DefaultFreudTheme.dimension.dp240
    val narrowPadding = FreudTheme.DefaultFreudTheme.dimension.dp24
    val widePadding = FreudTheme.DefaultFreudTheme.dimension.dp48
    val spacingLottieToText = FreudTheme.DefaultFreudTheme.dimension.dp24
    val spacingTitleToBody = FreudTheme.DefaultFreudTheme.dimension.dp8
    val spacingBodyToButton = FreudTheme.DefaultFreudTheme.dimension.dp24
    val spacingLottieToBlock = FreudTheme.DefaultFreudTheme.dimension.dp36

    const val TRANSITION_DURATION_MILLIS = 350
    const val AUTO_RETRY_MILLIS = 10_000L
}

object FreudFallbackTestTags {
    const val STUB_ROBOT = "freud_fallback_stub_root"
    const val TRY_AGAIN_BUTTON = "freud_fallback_try_again_button"
    const val CONTENT_SLOT = "freud_fallback_content_slot"
}

@Composable
fun FreudFallback(
    isError: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val isOnline = rememberIsOnline()

    FreudFallbackContent(
        isError = isError,
        isOnline = isOnline,
        onRetry = onRetry,
        modifier = modifier,
        content = content,
    )
}

@Composable
@Suppress("CyclomaticComplexMethod")
internal fun FreudFallbackContent(
    isError: Boolean,
    isOnline: Boolean,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val stubKind = when {
        !isError -> null
        isOnline -> FreudFallbackStubKind.Error
        else -> FreudFallbackStubKind.Network
    }

    val currentOnRetry by rememberUpdatedState(onRetry)
    val currentIsOnline by rememberUpdatedState(isOnline)
    var isAutoRetryEnabled by remember(stubKind) { mutableStateOf(stubKind != null) }

    LaunchedEffect(stubKind, isAutoRetryEnabled) {
        if (stubKind == null || !isAutoRetryEnabled) return@LaunchedEffect

        while (isAutoRetryEnabled) {
            delay(FreudFallbackDefaults.AUTO_RETRY_MILLIS)

            if (!isAutoRetryEnabled) break

            when (stubKind) {
                FreudFallbackStubKind.Error -> currentOnRetry()
                FreudFallbackStubKind.Network ->
                    if (currentIsOnline) {
                        currentOnRetry()
                    }
            }
        }
    }

    val resolvedOnRetry: () -> Unit = {
        isAutoRetryEnabled = false
        currentOnRetry()
    }

    val backgroundColor = FreudFallbackTheme.colorScheme.background
    val titleColor = FreudFallbackTheme.colorScheme.title
    val bodyColor = FreudFallbackTheme.colorScheme.body
    val buttonContainerColor = FreudFallbackTheme.colorScheme.buttonContainer
    val buttonContentColor = FreudFallbackTheme.colorScheme.buttonContent

    AnimatedContent(
        targetState = stubKind,
        transitionSpec = {
            val enter = fadeIn(tween(FreudFallbackDefaults.TRANSITION_DURATION_MILLIS)) +
                slideInVertically(tween(FreudFallbackDefaults.TRANSITION_DURATION_MILLIS)) { it / 8 }
            val exit = fadeOut(tween(FreudFallbackDefaults.TRANSITION_DURATION_MILLIS)) +
                slideOutVertically(tween(FreudFallbackDefaults.TRANSITION_DURATION_MILLIS)) { -it / 8 }
            enter togetherWith exit
        },
        contentKey = { it },
        modifier = modifier,
        label = "FreudFallback",
    ) { currentStub ->
        if (currentStub == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(FreudFallbackTestTags.CONTENT_SLOT),
            ) {
                content()
            }
        } else {
            FreudFallbackStubContent(
                kind = currentStub,
                onRetry = resolvedOnRetry,
                backgroundColor = backgroundColor,
                titleColor = titleColor,
                bodyColor = bodyColor,
                buttonContainerColor = buttonContainerColor,
                buttonContentColor = buttonContentColor,
            )
        }
    }
}

@Composable
private fun FreudFallbackStubContent(
    kind: FreudFallbackStubKind,
    onRetry: () -> Unit,
    backgroundColor: FreudDsToken<Color>,
    titleColor: FreudDsToken<Color>,
    bodyColor: FreudDsToken<Color>,
    buttonContainerColor: FreudDsToken<Color>,
    buttonContentColor: FreudDsToken<Color>,
) {
    val title = when (kind) {
        FreudFallbackStubKind.Error -> stringResource(Res.string.freud_fallback_error_title)
        FreudFallbackStubKind.Network -> stringResource(Res.string.freud_fallback_network_title)
    }
    val body = when (kind) {
        FreudFallbackStubKind.Error -> stringResource(Res.string.freud_fallback_error_body)
        FreudFallbackStubKind.Network -> stringResource(Res.string.freud_fallback_network_body)
    }
    val retryLabel = stringResource(Res.string.freud_fallback_retry_button)
    val stubContentDescription = "$title. $body"
    val isWide = isWide()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = backgroundColor,
                pattern = FreudBackgroundPattern.Waves,
            )
            .testTag(FreudFallbackTestTags.STUB_ROBOT)
            .semantics { contentDescription = stubContentDescription },
        contentAlignment = Alignment.Center,
    ) {
        if (isWide) {
            FreudFallbackWideLayout(
                title = title,
                body = body,
                retryLabel = retryLabel,
                onRetry = onRetry,
                titleColor = titleColor,
                bodyColor = bodyColor,
                buttonContainerColor = buttonContainerColor,
                buttonContentColor = buttonContentColor,
            )
        } else {
            FreudFallbackNarrowLayout(
                title = title,
                body = body,
                retryLabel = retryLabel,
                onRetry = onRetry,
                titleColor = titleColor,
                bodyColor = bodyColor,
                buttonContainerColor = buttonContainerColor,
                buttonContentColor = buttonContentColor,
            )
        }
    }
}

@Composable
private fun FreudFallbackNarrowLayout(
    title: String,
    body: String,
    retryLabel: String,
    onRetry: () -> Unit,
    titleColor: FreudDsToken<Color>,
    bodyColor: FreudDsToken<Color>,
    buttonContainerColor: FreudDsToken<Color>,
    buttonContentColor: FreudDsToken<Color>,
) {
    Column(
        modifier = Modifier.padding(horizontal = FreudFallbackDefaults.narrowPadding.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FreudFallbackLottie(
            modifier = Modifier.size(FreudFallbackDefaults.lottieSize.value),
        )

        FreudSpacer(size = FreudFallbackDefaults.spacingLottieToText)

        FreudFallbackTextBlock(
            title = title,
            body = body,
            titleColor = titleColor,
            bodyColor = bodyColor,
        )

        FreudSpacer(size = FreudFallbackDefaults.spacingBodyToButton)

        FreudHorizontalButton(
            text = retryLabel,
            onClick = onRetry,
            containerColor = buttonContainerColor,
            contentColor = buttonContentColor,
            size = FreudHorizontalButtonSize.LARGE,
            modifier = Modifier
                .width(FreudFallbackDefaults.buttonWidth.value)
                .testTag(FreudFallbackTestTags.TRY_AGAIN_BUTTON),
        )
    }
}

@Composable
private fun FreudFallbackWideLayout(
    title: String,
    body: String,
    retryLabel: String,
    onRetry: () -> Unit,
    titleColor: FreudDsToken<Color>,
    bodyColor: FreudDsToken<Color>,
    buttonContainerColor: FreudDsToken<Color>,
    buttonContentColor: FreudDsToken<Color>,
) {
    Column(
        modifier = Modifier.padding(horizontal = FreudFallbackDefaults.widePadding.value),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.widthIn(max = FreudFallbackDefaults.contentMaxWidth.value),
            horizontalArrangement = Arrangement.spacedBy(FreudFallbackDefaults.spacingLottieToBlock.value),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FreudFallbackLottie(
                modifier = Modifier.size(FreudFallbackDefaults.lottieSize.value),
            )

            Column(
                modifier = Modifier.widthIn(max = FreudFallbackDefaults.textMaxWidth.value),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                FreudFallbackTextBlock(
                    title = title,
                    body = body,
                    titleColor = titleColor,
                    bodyColor = bodyColor,
                )

                FreudSpacer(size = FreudFallbackDefaults.spacingBodyToButton)

                FreudHorizontalButton(
                    text = retryLabel,
                    onClick = onRetry,
                    containerColor = buttonContainerColor,
                    contentColor = buttonContentColor,
                    size = FreudHorizontalButtonSize.LARGE,
                    modifier = Modifier
                        .width(FreudFallbackDefaults.buttonWidth.value)
                        .testTag(FreudFallbackTestTags.TRY_AGAIN_BUTTON),
                )
            }
        }
    }
}

@Composable
private fun FreudFallbackLottie(modifier: Modifier = Modifier) {
    FreudLottie(
        reader = { Res.readBytes(LOTTIE_STUB_ROBOT_PATH) },
        source = FreudLottieSource.DotLottie,
        iterations = 1,
        isRestartable = true,
        modifier = modifier,
    )
}

@Composable
private fun ColumnScope.FreudFallbackTextBlock(
    title: String,
    body: String,
    titleColor: FreudDsToken<Color>,
    bodyColor: FreudDsToken<Color>,
) {
    val ds = FreudTheme.DefaultFreudTheme

    FreudText(
        value = title,
        color = titleColor,
        typography = ds.typography.textLgExtraBold,
        align = TextAlign.Center,
    )

    FreudSpacer(size = FreudFallbackDefaults.spacingTitleToBody)

    FreudText(
        value = body,
        color = bodyColor,
        typography = ds.typography.textSmSemiBold,
        align = TextAlign.Center,
    )
}

private object FreudFallbackTheme : FreudTheme() {
    val FreudColorScheme.background: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown100,
        )

    val FreudColorScheme.title: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown20,
        )

    val FreudColorScheme.body: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.gray70,
            dark = super.color.gray40,
        )

    val FreudColorScheme.buttonContainer: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown80,
            dark = super.color.brown60,
        )

    val FreudColorScheme.buttonContent: FreudDsToken<Color>
        @Composable get() = colorScheme provides FreudDynamicColor(
            light = super.color.brown10,
            dark = super.color.brown10,
        )
}

@Composable
internal expect fun rememberIsOnline(): Boolean

private const val LOTTIE_STUB_ROBOT_PATH = "files/lottie_stub_robot.lottie"
