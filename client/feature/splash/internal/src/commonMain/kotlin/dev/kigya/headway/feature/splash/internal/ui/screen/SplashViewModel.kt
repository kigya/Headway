package dev.kigya.headway.feature.splash.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.core.session.domain.usecase.ResolveLaunchDestinationUseCase
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
    resolveLaunchDestination: ResolveLaunchDestinationUseCase,
) : ViewModel() {
    private val store: SplashStore = SplashStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
        resolveLaunchDestination = resolveLaunchDestination,
    ).create(viewModelScope)

    val uiState: StateFlow<SplashStore.State> = store.stateFlow(viewModelScope)
}
