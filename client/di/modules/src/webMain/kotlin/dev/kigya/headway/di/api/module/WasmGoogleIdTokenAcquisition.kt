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

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> {
        val credential = awaitGoogleCredentialOrNull()
        return if (credential.isNullOrBlank()) {
            Outcome.failure(SessionDomainError.GoogleSignInUnavailable)
        } else {
            Outcome.success(credential)
        }
    }

    private suspend fun awaitGoogleCredentialOrNull(): String? =
        suspendCancellableCoroutine { continuation ->
            val promise = runCatching {
                headwayBrowserWindow().headwayStartGoogleCredentialFlow(GOOGLE_WEB_CLIENT_ID)
            }.getOrElse {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }
            promise.then(
                onFulfilled = { value: JsAny? ->
                    val text = value?.toString()
                    continuation.resume(text)
                    null
                },
                onRejected = { _: JsAny? ->
                    continuation.resume(null)
                    null
                },
            )
        }
}

private external interface HeadwayWindowExport : JsAny {
    fun headwayStartGoogleCredentialFlow(clientId: String): Promise<JsAny?>
}

private fun headwayWasmGlobalWindow(): JsAny = js("window")

private fun headwayBrowserWindow(): HeadwayWindowExport =
    headwayWasmGlobalWindow().unsafeCast<HeadwayWindowExport>()

private const val GOOGLE_WEB_CLIENT_ID: String =
    "658272377808-alf63nc9km4tjgv0dr6lltfqfo1jshqb.apps.googleusercontent.com"
