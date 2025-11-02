package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.feature.auth.api.AuthScreenKey
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashStore.Intent
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashStore.Label
import dev.kigya.headway.feature.splash.internal.ui.screen.SplashStore.State
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.asNavigationAsyncRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

interface SplashStore : Store<Intent, State, Label> {
    sealed interface Intent

    sealed interface Label

    @Immutable
    data class State(val shouldDisplayText: Boolean = false)
}

class SplashStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
) {
    fun create(executorCoroutineScope: CoroutineScope): SplashStore =
        object : SplashStore, Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper {
                launch {
                    delay(showTextDelay)
                    dispatch(Action.ShowText)
                }
            },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onAction<Action.ShowText> {
                    launch {
                        dispatch(Message.ShowText)
                        delay(afterTextDelay)
                        executorCoroutineScope.launch {
                            navigator.navigate(
                                NavigationIntent.ReplaceTopBy(
                                    screenNavigationKey = AuthScreenKey,
                                    asyncRunner = { executorCoroutineScope.asNavigationAsyncRunner() },
                                )
                            )
                        }
                    }
                }
            },
            reducer = Reducer { message ->
                when (message) {
                    Message.ShowText -> copy(shouldDisplayText = true)
                }
            },
        ) {}

    private sealed interface Action {
        object ShowText : Action
    }

    private sealed interface Message {
        object ShowText : Message
    }

    private companion object {
        val showTextDelay = 1_200.milliseconds
        val afterTextDelay = 800.milliseconds
    }
}
