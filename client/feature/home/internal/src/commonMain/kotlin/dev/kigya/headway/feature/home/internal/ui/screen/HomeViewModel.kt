package dev.kigya.headway.feature.home.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.core.session.domain.usecase.LoadHomeScreenSummaryUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutRegisteredUseCase
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
    loadHomeScreenSummary: LoadHomeScreenSummaryUseCase,
    logoutRegistered: LogoutRegisteredUseCase,
) : ViewModel() {

    private val store: HomeStore = HomeStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
        loadHomeScreenSummary = loadHomeScreenSummary,
        logoutRegistered = logoutRegistered,
    ).create(viewModelScope)

    val uiState: StateFlow<HomeStore.State> = store.stateFlow(viewModelScope)

    fun onSignOut() {
        store.accept(HomeStore.Intent.SignOut)
    }

    fun onRetryLoad() {
        store.accept(HomeStore.Intent.RetryLoad)
    }
}
