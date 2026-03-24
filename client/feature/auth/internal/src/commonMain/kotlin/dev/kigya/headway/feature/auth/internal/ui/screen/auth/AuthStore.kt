package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenKey
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.Intent
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.Label
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.State
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.CoroutineScope

interface AuthStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object OpenNoAccess : Intent
    }

    sealed interface Label

    @Immutable
    data class State(val shouldDisplayText: Boolean = false)
}

class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
) {
    fun create(executorCoroutineScope: CoroutineScope): AuthStore = object :
        AuthStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, Message, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onIntent<Intent.OpenNoAccess> {
                    navigator.navigate(NavigationIntent.NavigateTo(AuthNoAccessScreenKey))
                }
            },
            reducer = Reducer { message ->
                copy(shouldDisplayText = true)
            },
        ) {}

    private sealed interface Action

    private sealed interface Message
}
