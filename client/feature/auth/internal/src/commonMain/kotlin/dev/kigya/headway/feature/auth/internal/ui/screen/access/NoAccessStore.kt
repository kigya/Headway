package dev.kigya.headway.feature.auth.internal.ui.screen.access

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.feature.auth.internal.ui.screen.access.NoAccessStore.Intent
import dev.kigya.headway.feature.auth.internal.ui.screen.access.NoAccessStore.Label
import dev.kigya.headway.feature.auth.internal.ui.screen.access.NoAccessStore.State
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.CoroutineScope

interface NoAccessStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object Back : Intent
    }

    sealed interface Label

    @Immutable
    data object State
}

class AuthNoAccessStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
) {
    fun create(executorCoroutineScope: CoroutineScope): NoAccessStore = object : NoAccessStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State,
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onIntent<Intent.Back> {
                    navigator.navigate(NavigationIntent.NavigateBack)
                }
            },
            reducer = Reducer { this },
        ) {}

    private sealed interface Action

    private sealed interface Message
}

