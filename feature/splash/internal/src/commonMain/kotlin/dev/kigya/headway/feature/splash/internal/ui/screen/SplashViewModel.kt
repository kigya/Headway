package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SplashViewModel(storeFactory: StoreFactory) : ViewModel() {
    private val store: SplashStore = SplashStoreFactory(storeFactory).create(viewModelScope)

    val uiState: StateFlow<SplashStore.State> = store.stateFlow(viewModelScope)
}
