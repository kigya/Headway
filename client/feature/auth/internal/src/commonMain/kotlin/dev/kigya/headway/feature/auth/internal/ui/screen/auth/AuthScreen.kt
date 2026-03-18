package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.kigya.headway.core.designSystem.component.FreudFallback
import dev.kigya.headway.core.designSystem.component.FreudText
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.authBackground
import dev.kigya.headway.feature.auth.internal.ui.theme.auth.AuthTheme.brandTextColor
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
    var isError by remember { mutableStateOf(false) }
    FreudFallback(
        isError = isError,
        onRetry = {
            isError = false
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AuthTheme.colorScheme.authBackground.value)
                .clickable { onOpenNoAccess() },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            FreudText(
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { isError = true },
                value = "Auth Screen",
                color = AuthTheme.colorScheme.brandTextColor,
                typography = AuthTheme.typography.headingSmExtraBold,
            )
        }
    }
}
