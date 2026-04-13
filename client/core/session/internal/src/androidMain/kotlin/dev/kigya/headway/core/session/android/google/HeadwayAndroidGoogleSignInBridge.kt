package dev.kigya.headway.core.session.android.google

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.error.SessionDomainError

class HeadwayAndroidGoogleSignInBridge {

    @Volatile
    private var host: HeadwayAndroidGoogleSignInFlow? = null

    fun attach(host: HeadwayAndroidGoogleSignInFlow) {
        this.host = host
    }

    fun detach(host: HeadwayAndroidGoogleSignInFlow) {
        if (this.host === host) {
            this.host = null
        }
    }

    fun cancelPendingSignIn() {
        host?.cancelPendingGoogleSignIn()
    }

    fun beginSignInForIdToken(onResult: (Outcome<SessionDomainError, String>) -> Unit) {
        val current = host
        if (current == null) {
            onResult(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
        } else {
            current.beginGoogleSignInForIdToken(onResult)
        }
    }
}
