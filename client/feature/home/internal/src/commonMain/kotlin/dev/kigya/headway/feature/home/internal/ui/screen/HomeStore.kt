package dev.kigya.headway.feature.home.internal.ui.screen

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.usecase.LoadHomeScreenSummaryUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutRegisteredUseCase
import dev.kigya.headway.core.session.model.HomeActionSemanticType
import dev.kigya.headway.core.session.model.HomeScreenSummary
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.Intent
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.Label
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.State
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenKey
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.asNavigationAsyncRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface HomeStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object SignOut : Intent
        data object RetryLoad : Intent
        data class ActionClick(val semantic: HomeActionSemanticType) : Intent
        data object ToggleWideNavigation : Intent
    }

    sealed interface Label

    @Immutable
    data class State(
        val isLoading: Boolean = true,
        val summary: HomeScreenSummary? = null,
        val errorMessage: String? = null,
        val isWideNavigationVisible: Boolean = true,
    )
}

class HomeStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
    private val loadHomeScreenSummary: LoadHomeScreenSummaryUseCase,
    private val logoutRegistered: LogoutRegisteredUseCase,
) {
    fun create(executorCoroutineScope: CoroutineScope): HomeStore = object :
        HomeStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.LoadHomeSummary)
            },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onAction<Action.LoadHomeSummary> {
                    launch {
                        applyHomeSummaryResult(::dispatch)
                    }
                }
                onIntent<Intent.SignOut> {
                    launch {
                        performSignOutToAuth(executorCoroutineScope)
                    }
                }
                onIntent<Intent.RetryLoad> {
                    dispatch(Message.ClearError)
                    dispatch(Message.LoadingStarted)
                    launch {
                        applyHomeSummaryResult(::dispatch)
                    }
                }
                onIntent<Intent.ActionClick> { intent ->
                    when (intent.semantic) {
                        HomeActionSemanticType.SignOut -> launch {
                            performSignOutToAuth(executorCoroutineScope)
                        }
                        HomeActionSemanticType.LearnQuestions ->
                            navigator.navigate(
                                NavigationIntent.NavigateTo(LearnQuestionsScreenKey),
                            )
                        HomeActionSemanticType.Home,
                        HomeActionSemanticType.StartTrainingSession,
                        HomeActionSemanticType.EmployeeProgressManagement,
                        HomeActionSemanticType.PeopleManagement,
                        HomeActionSemanticType.ViewStatistics,
                        HomeActionSemanticType.About,
                        -> Unit
                    }
                }
                onIntent<Intent.ToggleWideNavigation> {
                    dispatch(Message.WideNavigationToggled)
                }
            },
            reducer = Reducer { message ->
                when (message) {
                    Message.LoadingStarted -> copy(isLoading = true)
                    is Message.SummaryLoaded -> copy(
                        isLoading = false,
                        summary = message.value,
                        errorMessage = null,
                    )
                    Message.LoadFailed -> copy(
                        isLoading = false,
                        errorMessage = LOAD_FAILED_MESSAGE,
                    )
                    Message.ClearError -> copy(errorMessage = null)
                    Message.WideNavigationToggled -> copy(
                        isWideNavigationVisible = !isWideNavigationVisible,
                    )
                }
            },
        ) {}

    private suspend fun applyHomeSummaryResult(dispatch: (Message) -> Unit) {
        when (val outcome = loadHomeScreenSummary()) {
            is Outcome.Success -> dispatch(Message.SummaryLoaded(outcome.value))
            is Outcome.Failure -> dispatch(Message.LoadFailed)
        }
    }

    private suspend fun performSignOutToAuth(executorCoroutineScope: CoroutineScope) {
        logoutRegistered()
        navigator.navigate(
            NavigationIntent.ReplaceTopBy(
                screenNavigationKey = AuthScreenKey,
                asyncRunner = { executorCoroutineScope.asNavigationAsyncRunner() },
            ),
        )
    }

    private sealed interface Action {
        data object LoadHomeSummary : Action
    }

    private sealed interface Message {
        data object LoadingStarted : Message
        data class SummaryLoaded(val value: HomeScreenSummary) : Message
        data object LoadFailed : Message
        data object ClearError : Message
        data object WideNavigationToggled : Message
    }

    private companion object {
        const val LOAD_FAILED_MESSAGE = "Could not load home."
    }
}
