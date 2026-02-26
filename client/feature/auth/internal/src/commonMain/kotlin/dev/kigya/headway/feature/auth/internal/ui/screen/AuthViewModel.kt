package dev.kigya.headway.feature.auth.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(storeFactory: StoreFactory) : ViewModel() {
    private val store: AuthStore = AuthStoreFactory(storeFactory).create(viewModelScope)

    val uiState: StateFlow<AuthStore.State> = store.stateFlow(viewModelScope)
}
