package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

internal interface SplashStore : Store<SplashStore.Intent, SplashStore.State, SplashStore.Label> {
    sealed interface Intent

    sealed interface Label

    @Immutable
    data class State(val shouldDisplayText: Boolean = false)
}

internal class SplashStoreFactory(
    private val storeFactory: StoreFactory,
) {
    fun create(executorCoroutineScope: CoroutineScope): SplashStore =
        object : SplashStore, Store<SplashStore.Intent, SplashStore.State, SplashStore.Label>
        by storeFactory.create<SplashStore.Intent, Action, Message, SplashStore.State, SplashStore.Label>(
            name = this::class.simpleName,
            initialState = SplashStore.State(),

            bootstrapper = coroutineBootstrapper {
                launch {
                    delay(showTextDelay)
                    dispatch(Action.ShowText)
                }
            },

            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onAction<Action.ShowText> {
                    dispatch(Message.ShowText)
                }
            },

            reducer = Reducer { message ->
                when (message) {
                    Message.ShowText -> copy(shouldDisplayText = true)
                }
            }
        ) {}

    private sealed interface Action {
        object ShowText : Action
    }

    private sealed interface Message {
        object ShowText : Message
    }

    private companion object {
        val showTextDelay = 1_200.milliseconds
    }
}
