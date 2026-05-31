package dev.kigya.headway.feature.auth.internal.ui.screen.auth

import androidx.compose.runtime.Immutable
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineExecutorFactory
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import dev.kigya.headway.core.session.domain.usecase.LoginAsGuestUseCase
import dev.kigya.headway.core.session.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.core.session.domain.usecase.ObtainGoogleIdTokenUseCase
import dev.kigya.headway.feature.auth.api.AuthNoAccessScreenKey
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.Intent
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.Label
import dev.kigya.headway.feature.auth.internal.ui.screen.auth.AuthStore.State
import dev.kigya.headway.feature.home.api.HomeScreenKey
import dev.kigya.headway.feature.learnQuestions.api.LearnQuestionsScreenKey
import dev.kigya.headway.navigation.api.navigator.NavigationIntent
import dev.kigya.headway.navigation.api.navigator.NavigatorContract
import dev.kigya.headway.navigation.api.navigator.asNavigationAsyncRunner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.jvm.JvmInline

interface AuthStore : Store<Intent, State, Label> {
    sealed interface Intent {
        data object SignInWithGoogle : Intent
        data object ContinueAsGuest : Intent
        data object DismissError : Intent
    }

    sealed interface Label

    @Immutable
    data class State(
        val isBusy: Boolean = false,
        val hasError: Boolean = false,
    )
}

class AuthStoreFactory(
    private val storeFactory: StoreFactory,
    private val navigator: NavigatorContract,
    private val obtainGoogleIdToken: ObtainGoogleIdTokenUseCase,
    private val loginWithGoogle: LoginWithGoogleUseCase,
    private val loginAsGuest: LoginAsGuestUseCase,
) {
    fun create(executorCoroutineScope: CoroutineScope): AuthStore = object :
        AuthStore,
        Store<Intent, State, Label>
        by storeFactory.create<Intent, Action, AuthReducerMessage, State, Label>(
            name = this::class.simpleName,
            initialState = State(),
            bootstrapper = coroutineBootstrapper { },
            executorFactory = coroutineExecutorFactory(executorCoroutineScope.coroutineContext) {
                onIntent<Intent.DismissError> {
                    dispatch(AuthReducerSetHasError(false))
                }
                onIntent<Intent.SignInWithGoogle> {
                    launch {
                        dispatch(AuthReducerSetBusy(true))
                        dispatch(AuthReducerSetHasError(false))
                        when (val tokenOutcome = obtainGoogleIdToken()) {
                            is Outcome.Failure -> {
                                when (tokenOutcome.error) {
                                    SessionDomainError.GoogleSignInCancelled ->
                                        dispatch(AuthReducerSetBusy(false))

                                    else -> {
                                        dispatch(AuthReducerSetHasError(true))
                                        dispatch(AuthReducerSetBusy(false))
                                    }
                                }
                            }

                            is Outcome.Success ->
                                when (val signedIn = loginWithGoogle(tokenOutcome.value)) {
                                    is Outcome.Success ->
                                        navigator.navigate(
                                            NavigationIntent.ReplaceTopBy(
                                                screenNavigationKey = HomeScreenKey,
                                                asyncRunner = { executorCoroutineScope.asNavigationAsyncRunner() },
                                            ),
                                        )

                                    is Outcome.Failure ->
                                        when (signedIn.error) {
                                            SessionDomainError.UserNotInvited -> {
                                                navigator.navigate(
                                                    NavigationIntent.NavigateTo(AuthNoAccessScreenKey),
                                                )
                                                dispatch(AuthReducerSetBusy(false))
                                            }

                                            else -> {
                                                dispatch(AuthReducerSetHasError(true))
                                                dispatch(AuthReducerSetBusy(false))
                                            }
                                        }
                                }
                        }
                    }
                }
                onIntent<Intent.ContinueAsGuest> {
                    launch {
                        dispatch(AuthReducerSetBusy(true))
                        dispatch(AuthReducerSetHasError(false))
                        when (loginAsGuest()) {
                            is Outcome.Success ->
                                navigator.navigate(
                                    NavigationIntent.ReplaceTopBy(
                                        screenNavigationKey = LearnQuestionsScreenKey,
                                        asyncRunner = { executorCoroutineScope.asNavigationAsyncRunner() },
                                    ),
                                )

                            is Outcome.Failure -> {
                                dispatch(AuthReducerSetHasError(true))
                                dispatch(AuthReducerSetBusy(false))
                            }
                        }
                    }
                }
            },
            reducer = Reducer { message ->
                when (message) {
                    is AuthReducerSetBusy -> copy(isBusy = message.value)
                    is AuthReducerSetHasError -> copy(hasError = message.value)
                }
            },
        ) {}

    private sealed interface Action
}

private sealed interface AuthReducerMessage

@JvmInline
private value class AuthReducerSetBusy(val value: Boolean) : AuthReducerMessage

@JvmInline
private value class AuthReducerSetHasError(val value: Boolean) : AuthReducerMessage
