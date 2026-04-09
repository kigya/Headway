package dev.kigya.headway

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume

private var headwayIosGoogleSignInRunner: (() -> Unit)? = null

private var headwayIosGoogleAwaiting: CancellableContinuation<Outcome<SessionDomainError, String>>? = null

fun headwayIosRegisterGoogleSignInRunner(run: () -> Unit) {
    headwayIosGoogleSignInRunner = run
}

fun headwayIosOnGoogleSignInSuccess(idToken: String) {
    val continuation = headwayIosGoogleAwaiting
    headwayIosGoogleAwaiting = null
    continuation?.resume(Outcome.success(idToken))
}

fun headwayIosOnGoogleSignInUserCancelled() {
    val continuation = headwayIosGoogleAwaiting
    headwayIosGoogleAwaiting = null
    continuation?.resume(Outcome.failure(SessionDomainError.GoogleSignInCancelled))
}

fun headwayIosOnGoogleSignInUnavailable() {
    val continuation = headwayIosGoogleAwaiting
    headwayIosGoogleAwaiting = null
    continuation?.resume(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
}

internal suspend fun headwayIosSuspendForGoogleIdToken(): Outcome<SessionDomainError, String> =
    suspendCancellableCoroutine { continuation ->
        headwayIosGoogleAwaiting?.cancel(CancellationException("replaced"))
        headwayIosGoogleAwaiting = continuation
        continuation.invokeOnCancellation {
            if (headwayIosGoogleAwaiting === continuation) {
                headwayIosGoogleAwaiting = null
            }
        }
        val runner = headwayIosGoogleSignInRunner
        if (runner == null) {
            headwayIosGoogleAwaiting = null
            continuation.resume(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
        } else {
            runner()
        }
    }

internal class IosGoogleIdTokenAcquisition : GoogleIdTokenAcquisitionContract {

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> =
        headwayIosSuspendForGoogleIdToken()
}
