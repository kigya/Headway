package dev.kigya.headway.feature.learnQuestions.internal.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import dev.kigya.headway.core.session.domain.usecase.LoadGuestLearningOverviewUseCase
import dev.kigya.headway.core.session.domain.usecase.LogoutGuestUseCase
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import kotlinx.coroutines.flow.StateFlow

class LearnQuestionsViewModel(
    storeFactory: StoreFactory,
    navigator: NavigatorContract,
    loadGuestLearningOverview: LoadGuestLearningOverviewUseCase,
    logoutGuest: LogoutGuestUseCase,
) : ViewModel() {

    private val store: LearnQuestionsStore = LearnQuestionsStoreFactory(
        storeFactory = storeFactory,
        navigator = navigator,
        loadGuestLearningOverview = loadGuestLearningOverview,
        logoutGuest = logoutGuest,
    ).create(viewModelScope)

    val uiState: StateFlow<LearnQuestionsStore.State> = store.stateFlow(viewModelScope)

    fun onGuestSignOut() {
        store.accept(LearnQuestionsStore.Intent.GuestSignOut)
    }

    fun onRetryLoad() {
        store.accept(LearnQuestionsStore.Intent.RetryLoad)
    }
}
