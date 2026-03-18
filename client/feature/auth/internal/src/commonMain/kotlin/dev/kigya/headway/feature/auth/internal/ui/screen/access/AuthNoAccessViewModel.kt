package dev.kigya.headway.feature.auth.internal.ui.screen.access

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class AuthNoAccessViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
) : ViewModel() {
    private val store: NoAccessStore = AuthNoAccessStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
    ).create(viewModelScope)

    val uiState: StateFlow<NoAccessStore.State> = store.stateFlow(viewModelScope)

    fun onBack() {
        store.accept(NoAccessStore.Intent.Back)
    }
}
