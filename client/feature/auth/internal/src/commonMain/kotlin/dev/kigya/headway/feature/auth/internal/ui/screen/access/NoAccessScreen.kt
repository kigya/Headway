package dev.kigya.headway.feature.auth.internal.ui.screen.access

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.kigya.headway.core.designSystem.component.FreudButtonIconSpec
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudIcon
import dev.kigya.headway.core.designSystem.component.FreudLottie
import dev.kigya.headway.core.designSystem.component.FreudLottieSource
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.util.FreudBackgroundPattern
import dev.kigya.headway.core.designSystem.util.background
import dev.kigya.headway.core.designSystem.util.isWide
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.arcOverlay
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.backIconTint
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.buttonContainer
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.buttonContent
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.cardBackgroundNarrow
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.cardBackgroundWide
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.reportIconTint
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.screenBackgroundNarrow
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.screenBackgroundWide
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.subtitle
import dev.kigya.headway.feature.auth.internal.ui.theme.access.AuthNoAccessTheme.title
import headway.feature.auth.internal.generated.resources.Res
import headway.feature.auth.internal.generated.resources.auth_ask_manager
import headway.feature.auth.internal.generated.resources.auth_back_icon_content_description
import headway.feature.auth.internal.generated.resources.auth_manager_hasnt_added_to_the_system
import headway.feature.auth.internal.generated.resources.auth_no_access
import headway.feature.auth.internal.generated.resources.ic_chevron_left
import headway.feature.auth.internal.generated.resources.ic_report
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Suppress("ExplicitDependencies")
@Composable
internal fun NoAccessScreen() {
    val viewModel = koinViewModel<AuthNoAccessViewModel>()

    NoAccessScreenContent(
        onBack = viewModel::onBack,
    )
}

@Suppress("ModifierOrder")
@Composable
private fun NoAccessScreenContent(
    onBack: () -> Unit,
) {
    val isWide = isWide()
    val backgroundColor = if (isWide) {
        AuthNoAccessTheme.colorScheme.screenBackgroundWide
    } else {
        AuthNoAccessTheme.colorScheme.screenBackgroundNarrow
    }
    val backgroundPattern = if (isWide) {
        FreudBackgroundPattern.Waves
    } else {
        FreudBackgroundPattern.None
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = backgroundColor,
                pattern = backgroundPattern,
            ),
    ) {
        if (isWide) {
            WideNoAccessLayout()
        } else {
            NarrowNoAccessLayout()
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(
                    vertical = AuthNoAccessTheme.dimension.dp36.value,
                    horizontal = AuthNoAccessTheme.dimension.dp16.value,
                )
                .clip(AuthNoAccessTheme.shape.circle.value)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            BackIcon()
        }
    }
}

@Composable
private fun NarrowNoAccessLayout() {
    val contentTopInset = NarrowOverlayHeight * ArcVisibleTopRatio

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = AuthNoAccessTheme.colorScheme.cardBackgroundNarrow,
                pattern = FreudBackgroundPattern.None,
            ),
    ) {
        NoAccessLottie(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(NarrowOverlayHeight),
        ) {
            NoAccessArcOverlay(
                modifier = Modifier.matchParentSize(),
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(
                        start = AuthNoAccessTheme.dimension.dp24.value,
                        end = AuthNoAccessTheme.dimension.dp24.value,
                        top = contentTopInset,
                    )
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center,
            ) {
                NoAccessTextAndButton(
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun WideNoAccessLayout() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .weight(WideLottiePaneWeight)
                .fillMaxHeight()
                .background(
                    color = AuthNoAccessTheme.colorScheme.cardBackgroundWide,
                    pattern = FreudBackgroundPattern.None,
                )
                .clipToBounds(),
            contentAlignment = Alignment.CenterStart,
        ) {
            NoAccessLottie(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = WideLottieMaxWidth),
                contentScale = ContentScale.FillHeight,
            )
        }

        Box(
            modifier = Modifier
                .weight(WideContentPaneWeight)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center,
        ) {
            NoAccessTextAndButton(
                modifier = Modifier
                    .padding(horizontal = WideContentHorizontalPadding)
                    .widthIn(max = WideContentMaxWidth),
                isWide = true,
            )
        }
    }
}

@Composable
private fun NoAccessLottie(
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    FreudLottie(
        reader = { Res.readBytes("files/lottie_no_access_screen.lottie") },
        source = FreudLottieSource.DotLottie,
        iterations = 1,
        modifier = modifier,
        contentScale = contentScale,
    )
}

@Composable
private fun NoAccessArcOverlay(
    modifier: Modifier = Modifier,
) {
    val overlayColor = AuthNoAccessTheme.colorScheme.arcOverlay.value

    Box(
        modifier = modifier.drawBehind {
            val startY = size.height * ArcStartYRatio

            val path = Path().apply {
                moveTo(0f, startY)
                quadraticTo(
                    x1 = size.width / 2f,
                    y1 = size.height * ArcControlYRatio,
                    x2 = size.width,
                    y2 = startY,
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawPath(
                path = path,
                color = overlayColor,
            )
        },
    )
}

@Suppress("EffectKeys")
@Composable
private fun NoAccessTextAndButton(
    modifier: Modifier = Modifier,
    isWide: Boolean = false,
) {
    val titleTypography = if (isWide) {
        AuthNoAccessTheme.typography.headingLgExtraBold
    } else {
        AuthNoAccessTheme.typography.headingSmExtraBold
    }

    val subtitleTypography = if (isWide) {
        AuthNoAccessTheme.typography.paragraphLg
    } else {
        AuthNoAccessTheme.typography.paragraphMd
    }

    val spaceAfterTitle = if (isWide) {
        AuthNoAccessTheme.dimension.dp16
    } else {
        AuthNoAccessTheme.dimension.dp8
    }

    val spaceAfterSubtitle = if (isWide) {
        AuthNoAccessTheme.dimension.dp32
    } else {
        AuthNoAccessTheme.dimension.dp24
    }

    val buttonModifier = if (isWide) {
        Modifier.width(WideButtonWidth)
    } else {
        Modifier.fillMaxWidth()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        FreudText(
            modifier = if (isWide) Modifier.fillMaxWidth() else Modifier,
            value = stringResource(Res.string.auth_no_access),
            color = AuthNoAccessTheme.colorScheme.title,
            typography = titleTypography,
            align = TextAlign.Center,
        )

        FreudSpacer(size = spaceAfterTitle)

        FreudText(
            modifier = Modifier.fillMaxWidth(),
            value = stringResource(Res.string.auth_manager_hasnt_added_to_the_system),
            color = AuthNoAccessTheme.colorScheme.subtitle,
            typography = subtitleTypography,
            align = TextAlign.Center,
        )

        FreudSpacer(size = spaceAfterSubtitle)

        var isVisible by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(REPORT_ANIMATION_DELAY_MILLIS)
            isVisible = true
        }

        FreudHorizontalButton(
            modifier = buttonModifier,
            text = stringResource(Res.string.auth_ask_manager),
            onClick = {},
            containerColor = AuthNoAccessTheme.colorScheme.buttonContainer,
            contentColor = AuthNoAccessTheme.colorScheme.buttonContent,
            size = FreudHorizontalButtonSize.LARGE,
            trailingIcon = FreudButtonIconSpec.Reveal(
                resource = Res.drawable.ic_report,
                contentDescription = null,
                tint = AuthNoAccessTheme.colorScheme.reportIconTint,
                isVisible = isVisible,
            ),
        )
    }
}

@Composable
private fun BackIcon() {
    FreudIcon(
        resource = Res.drawable.ic_chevron_left,
        contentDescription = Res.string.auth_back_icon_content_description,
        tint = AuthNoAccessTheme.colorScheme.backIconTint,
    )
}

private const val REPORT_ANIMATION_DELAY_MILLIS = 500L
private val NarrowOverlayHeight = 334.dp
private const val ArcStartYRatio = 0.42f
private const val ArcControlYRatio = -0.18f
private const val ArcVisibleTopRatio = (ArcStartYRatio + ArcControlYRatio) / 2f

private const val WideLottiePaneWeight = 1f
private const val WideContentPaneWeight = 2f
private val WideLottieMaxWidth = 520.dp
private val WideContentMaxWidth = 560.dp
private val WideButtonWidth = 336.dp
private val WideContentHorizontalPadding = 48.dp

