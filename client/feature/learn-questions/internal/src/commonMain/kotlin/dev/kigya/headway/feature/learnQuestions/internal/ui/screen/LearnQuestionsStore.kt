package dev.kigya.headway.feature.learnQuestions.internal.ui.screen

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.usecase.LoadGuestLearningOverviewUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutGuestUseCase
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsStore.Intent
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsStore.Label
import dev.kigya.headway.feature.learnQuestions.internal.ui.screen.LearnQuestionsStore.State
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.asNavigationAsyncRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface LearnQuestionsStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object GuestSignOut : Intent
        data object RetryLoad : Intent
    }

    sealed interface Label

    @Immutable
    data class State(
        val title: String = "",
        val detail: String = "",
        val errorMessage: String? = null,
    )
}

class LearnQuestionsStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
    private val loadGuestLearningOverview: LoadGuestLearningOverviewUseCase,
    private val logoutGuest: LogoutGuestUseCase,
) {
    fun create(executorCoroutineScope: CoroutineScope): LearnQuestionsStore = object :
        LearnQuestionsStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.LoadOverview)
            },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onAction<Action.LoadOverview> {
                    launch {
                        when (val overview = loadGuestLearningOverview()) {
                            is Outcome.Success -> dispatch(
                                Message.ContentLoaded(
                                    title = overview.value.headline,
                                    detail = overview.value.detailLine,
                                ),
                            )

                            is Outcome.Failure -> dispatch(Message.LoadFailed)
                        }
                    }
                }
                onIntent<Intent.RetryLoad> {
                    dispatch(Message.ClearError)
                    launch {
                        when (val overview = loadGuestLearningOverview()) {
                            is Outcome.Success -> dispatch(
                                Message.ContentLoaded(
                                    title = overview.value.headline,
                                    detail = overview.value.detailLine,
                                ),
                            )

                            is Outcome.Failure -> dispatch(Message.LoadFailed)
                        }
                    }
                }
                onIntent<Intent.GuestSignOut> {
                    launch {
                        logoutGuest()
                        navigator.navigate(
                            NavigationIntent.ReplaceTopBy(
                                screenNavigationKey = AuthScreenKey,
                                asyncRunner = { executorCoroutineScope.asNavigationAsyncRunner() },
                            ),
                        )
                    }
                }
            },
            reducer = Reducer { message ->
                when (message) {
                    is Message.ContentLoaded -> copy(
                        title = message.title,
                        detail = message.detail,
                        errorMessage = null,
                    )

                    Message.LoadFailed -> copy(errorMessage = "Could not load learning content.")
                    Message.ClearError -> copy(errorMessage = null)
                }
            },
        ) {}

    private sealed interface Action {
        data object LoadOverview : Action
    }

    private sealed interface Message {
        data class ContentLoaded(val title: String, val detail: String) : Message
        data object LoadFailed : Message
        data object ClearError : Message
    }
}
