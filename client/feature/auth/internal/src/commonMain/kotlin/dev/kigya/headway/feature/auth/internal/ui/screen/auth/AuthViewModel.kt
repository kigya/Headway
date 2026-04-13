package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.core.session.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.core.session.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.core.session.domain.usecase.ObtainGoogleIdTokenUseCase
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
    obtainGoogleIdToken: ObtainGoogleIdTokenUseCase,
    loginWithGoogle: LoginWithGoogleUseCase,
    loginAsGuest: LoginAsGuestUseCase,
) : ViewModel() {
    private val store: AuthStore = AuthStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
        obtainGoogleIdToken = obtainGoogleIdToken,
        loginWithGoogle = loginWithGoogle,
        loginAsGuest = loginAsGuest,
    ).create(viewModelScope)

    val uiState: StateFlow<AuthStore.State> = store.stateFlow(viewModelScope)

    fun onSignInWithGoogle() {
        store.accept(AuthStore.Intent.SignInWithGoogle)
    }

    fun onContinueAsGuest() {
        store.accept(AuthStore.Intent.ContinueAsGuest)
    }

    fun onDismissError() {
        store.accept(AuthStore.Intent.DismissError)
    }
}
