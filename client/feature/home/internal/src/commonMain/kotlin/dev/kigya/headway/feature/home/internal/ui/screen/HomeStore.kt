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
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.Intent
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.Label
import dev.kigya.headway.feature.home.internal.ui.screen.HomeStore.State
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.asNavigationAsyncRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

interface HomeStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object SignOut : Intent
        data object RetryLoad : Intent
    }

    sealed interface Label

    @Immutable
    data class State(
        val greeting: String = "",
        val errorMessage: String? = null,
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
                        when (val summary = loadHomeScreenSummary()) {
                            is Outcome.Success -> dispatch(Message.GreetingLoaded(summary.value.greeting))
                            is Outcome.Failure -> dispatch(Message.LoadFailed)
                        }
                    }
                }
                onIntent<Intent.RetryLoad> {
                    dispatch(Message.ClearError)
                    launch {
                        when (val summary = loadHomeScreenSummary()) {
                            is Outcome.Success -> dispatch(Message.GreetingLoaded(summary.value.greeting))
                            is Outcome.Failure -> dispatch(Message.LoadFailed)
                        }
                    }
                }
                onIntent<Intent.SignOut> {
                    launch {
                        logoutRegistered()
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
                    is Message.GreetingLoaded -> copy(greeting = message.value, errorMessage = null)
                    Message.LoadFailed -> copy(errorMessage = "Could not load home.")
                    Message.ClearError -> copy(errorMessage = null)
                }
            },
        ) {}

    private sealed interface Action {
        data object LoadHomeSummary : Action
    }

    private sealed interface Message {
        data class GreetingLoaded(val value: String) : Message
        data object LoadFailed : Message
        data object ClearError : Message
    }
}
