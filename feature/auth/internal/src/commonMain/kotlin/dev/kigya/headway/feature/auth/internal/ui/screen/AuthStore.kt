package dev.kigya.headway.feature.auth.internal.ui.screen

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.feature.auth.internal.ui.screen.AuthStore.Intent
import dev.kigya.headway.feature.auth.internal.ui.screen.AuthStore.Label
import dev.kigya.headway.feature.auth.internal.ui.screen.AuthStore.State
import kotlinx.coroutines.CoroutineScope

interface AuthStore : Store<Intent, State, Label> {
    sealed interface Intent

    sealed interface Label

    @Immutable
    data class State(val shouldDisplayText: Boolean = false)
}

class AuthStoreFactory(
    private val storeFactory: StoreFactory,
) {
    fun create(executorCoroutineScope: CoroutineScope): AuthStore = object :
        AuthStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
            },
            reducer = Reducer { message ->
                copy(shouldDisplayText = true)
            },
        ) {}

    private sealed interface Action

    private sealed interface Message
}
