package dev.kigya.headway.feature.learnQuestions.internal.ui.screen

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
import dev.kigya.headway.feature.learnQuestions.internal.ui.theme.LearnQuestionsTheme
import dev.kigya.headway.feature.learnQuestions.internal.ui.theme.LearnQuestionsTheme.learnActionContainer
import dev.kigya.headway.feature.learnQuestions.internal.ui.theme.LearnQuestionsTheme.learnActionContent
import dev.kigya.headway.feature.learnQuestions.internal.ui.theme.LearnQuestionsTheme.learnBackground
import dev.kigya.headway.feature.learnQuestions.internal.ui.theme.LearnQuestionsTheme.learnPrimaryText
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun LearnQuestionsScreen() {
    val viewModel = koinViewModel<LearnQuestionsViewModel>()
    val state = viewModel.uiState.collectAsStateWithLifecycle()

    LearnQuestionsScreenContent(
        state = state,
        onGuestSignOut = viewModel::onGuestSignOut,
        onRetryLoad = viewModel::onRetryLoad,
    )
}

@Composable
private fun LearnQuestionsScreenContent(
    state: State<LearnQuestionsStore.State>,
    onGuestSignOut: () -> Unit,
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
                    .background(LearnQuestionsTheme.colorScheme.learnBackground.value)
                    .padding(LearnQuestionsTheme.dimension.dp24.value),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FreudText(
                    value = FreudTextValue.text(state.value.title),
                    color = LearnQuestionsTheme.colorScheme.learnPrimaryText,
                    typography = LearnQuestionsTheme.typography.headingSmExtraBold,
                )
                FreudSpacer(size = LearnQuestionsTheme.dimension.dp12)
                FreudText(
                    value = FreudTextValue.text(state.value.detail),
                    color = LearnQuestionsTheme.colorScheme.learnPrimaryText,
                    typography = LearnQuestionsTheme.typography.paragraphLg,
                )
                FreudSpacer(size = LearnQuestionsTheme.dimension.dp24)
                FreudHorizontalButton(
                    text = FreudTextValue.text("Guest sign out"),
                    onClick = onGuestSignOut,
                    containerColor = LearnQuestionsTheme.colorScheme.learnActionContainer,
                    contentColor = LearnQuestionsTheme.colorScheme.learnActionContent,
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
