package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
) : ViewModel() {
    private val store: AuthStore = AuthStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
    ).create(viewModelScope)

    val uiState: StateFlow<AuthStore.State> = store.stateFlow(viewModelScope)

    fun onOpenNoAccess() {
        store.accept(AuthStore.Intent.OpenNoAccess)
    }
}
