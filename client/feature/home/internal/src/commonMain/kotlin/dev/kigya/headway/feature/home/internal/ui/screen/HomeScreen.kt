package dev.kigya.headway.feature.home.internal.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudFallback
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButton
import dev.kigya.headway.core.designSystem.component.FreudHorizontalButtonSize
import dev.kigya.headway.core.designSystem.component.FreudSpacer
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.core.designSystem.util.FreudScreenByWidth
import dev.kigya.headway.core.designSystem.util.FreudTextValue
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeActionContainer
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeActionContent
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homeBackground
import dev.kigya.headway.feature.home.internal.ui.theme.HomeTheme.homePrimaryText
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun HomeScreen() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        state = state,
        onSignOut = viewModel::onSignOut,
        onRetryLoad = viewModel::onRetryLoad,
    )
}

@Composable
private fun HomeScreenContent(
    state: State<HomeStore.State>,
    onSignOut: () -> Unit,
    onRetryLoad: () -> Unit,
) {
    val content: @Composable BoxScope.() -> Unit = {
        FreudFallback(
            isError = state.value.errorMessage != null,
            onRetry = onRetryLoad,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HomeTheme.colorScheme.homeBackground.value)
                    .padding(HomeTheme.dimension.dp24.value),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FreudText(
                    value = FreudTextValue.text(state.value.greeting),
                    color = HomeTheme.colorScheme.homePrimaryText,
                    typography = HomeTheme.typography.headingSmExtraBold,
                )
                FreudSpacer(size = HomeTheme.dimension.dp24)
                FreudHorizontalButton(
                    text = FreudTextValue.text("Sign out"),
                    onClick = onSignOut,
                    containerColor = HomeTheme.colorScheme.homeActionContainer,
                    contentColor = HomeTheme.colorScheme.homeActionContent,
                    size = FreudHorizontalButtonSize.LARGE,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }

    FreudScreenByWidth(
        narrow = content,
        wide = content,
    )
}
