package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
) : ViewModel() {
    private val store: SplashStore = SplashStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
    ).create(viewModelScope)

    val uiState: StateFlow<SplashStore.State> = store.stateFlow(viewModelScope)
}
