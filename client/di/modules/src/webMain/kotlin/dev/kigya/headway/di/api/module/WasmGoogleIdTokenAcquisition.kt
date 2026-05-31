@file:OptIn(ExperimentalWasmJsInterop::class)

package dev.kigya.headway.di.api.module

import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.js.JsAny
import kotlin.js.Promise
import kotlin.js.js
import kotlin.js.unsafeCast

internal class WasmGoogleIdTokenAcquisition : GoogleIdTokenAcquisitionContract {

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> =
        suspendCancellableCoroutine { continuation ->
            val promise = runCatching {
                headwayBrowserWindow().headwayStartGoogleCredentialFlow(GOOGLE_WEB_CLIENT_ID)
            }.getOrElse {
                continuation.resume(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                return@suspendCancellableCoroutine
            }
            promise.then(
                onFulfilled = { value: JsAny? ->
                    val credential = value?.toString()
                    if (credential.isNullOrBlank()) {
                        continuation.resume(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                    } else {
                        continuation.resume(Outcome.success(credential))
                    }
                    null
                },
                onRejected = { reason: JsAny? ->
                    continuation.resume(Outcome.failure(toGoogleSignInError(reason)))
                    null
                },
            )
        }
}

private fun toGoogleSignInError(reason: JsAny?): SessionDomainError {
    val message = reason?.toString().orEmpty()
    return if (message.contains(GOOGLE_SIGN_IN_CANCELLED_MARKER, ignoreCase = true)) {
        SessionDomainError.GoogleSignInCancelled
    } else {
        SessionDomainError.GoogleSignInUnavailable
    }
}

private external interface HeadwayWindowExport : JsAny {
    fun headwayStartGoogleCredentialFlow(clientId: String): Promise<JsAny?>
}

private fun headwayWasmGlobalWindow(): JsAny = js("window")

private fun headwayBrowserWindow(): HeadwayWindowExport =
    headwayWasmGlobalWindow().unsafeCast<HeadwayWindowExport>()

private const val GOOGLE_SIGN_IN_CANCELLED_MARKER: String = "cancelled"

private const val GOOGLE_WEB_CLIENT_ID: String =
    "658272377808-alf63nc9km4tjgv0dr6lltfqfo1jshqb.apps.googleusercontent.com"
