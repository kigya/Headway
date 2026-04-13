package dev.kigya.headway.core.session.android.google

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class HeadwayAndroidGoogleIdTokenAcquisition(
    private val bridge: HeadwayAndroidGoogleSignInBridge,
) : GoogleIdTokenAcquisitionContract {

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> =
        suspendCancellableCoroutine { continuation ->
            bridge.beginSignInForIdToken { outcome ->
                if (continuation.isActive) {
                    continuation.resume(outcome)
                }
            }
            continuation.invokeOnCancellation {
                bridge.cancelPendingSignIn()
            }
        }
}
