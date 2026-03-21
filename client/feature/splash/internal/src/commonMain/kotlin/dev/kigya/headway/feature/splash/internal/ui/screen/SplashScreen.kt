package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudLottie
import dev.kigya.headway.core.designSystem.component.FreudLottieSource
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.util.FreudScreenByWidth
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.feature.splash.internal.ui.theme.SplashTheme
import dev.kigya.headway.feature.splash.internal.ui.theme.SplashTheme.brandTextColor
import dev.kigya.headway.feature.splash.internal.ui.theme.SplashTheme.splashBackground
import headway.feature.splash.internal.generated.resources.Res
import headway.feature.splash.internal.generated.resources.splash_brand
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SplashScreen() {
    val viewModel = koinViewModel<SplashViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    SplashScreenContent(state)
}

@Composable
private fun SplashScreenContent(state: State<SplashStore.State>) {
    val content: @Composable BoxScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SplashTheme.colorScheme.splashBackground.value),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            FreudLottie(
                reader = { Res.readBytes("files/lottie_brand_logo.json") },
                source = FreudLottieSource.Json,
                modifier = Modifier.size(SplashTheme.dimension.dp72.value),
            )
            AnimatedVisibility(
                visible = state.value.shouldDisplayText,
                enter = fadeIn(tween()) + expandVertically(tween()),
            ) {
                FreudSpacer(size = SplashTheme.dimension.dp16)
                FreudText(
                    value = FreudTextValue.text(Res.string.splash_brand),
                    color = SplashTheme.colorScheme.brandTextColor,
                    typography = SplashTheme.typography.headingSmExtraBold,
                )
            }
        }
    }

    FreudScreenByWidth(
        narrow = content,
        wide = content,
    )
}
