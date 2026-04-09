package dev.kigya.headway.core.session.domain.usecase

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import dev.kigya.headway.core.outcome.Outcome
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.error.SessionDomainError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.awt.Desktop
import java.awt.EventQueue
import java.awt.Frame
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.URI
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import java.util.concurrent.atomic.AtomicBoolean

class DesktopGoogleIdTokenAcquisition(
    private val ioDispatcher: CoroutineDispatcher,
    private val googleOAuthClientSecret: String?,
) : GoogleIdTokenAcquisitionContract {

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> =
        withContext(ioDispatcher) {
            withTimeoutOrNull(OAUTH_WAIT_TIMEOUT_MILLIS) {
                suspendCancellableCoroutine<Outcome<SessionDomainError, String>> { continuation ->
                    val finished = AtomicBoolean(false)
                    var server: HttpServer? = null
                    fun finishOnce(outcome: Outcome<SessionDomainError, String>) {
                        if (!finished.compareAndSet(false, true)) {
                            return
                        }
                        runCatching { server?.stop(SERVER_STOP_DELAY_SECONDS) }
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.success(outcome))
                        }
                    }
                    continuation.invokeOnCancellation {
                        runCatching { server?.stop(SERVER_STOP_DELAY_SECONDS) }
                    }
                    val verifier = generateCodeVerifier()
                    val challenge = codeChallengeS256(verifier)
                    val state = randomUrlSafe(OAUTH_STATE_BYTE_LENGTH)
                    val redirectUri = OAUTH_REDIRECT_URI
                    val authUrl = buildAuthorizationUrl(
                        clientId = GOOGLE_WEB_CLIENT_ID,
                        redirectUri = redirectUri,
                        codeChallenge = challenge,
                        state = state,
                    )
                    val created = runCatching {
                        HttpServer.create(
                            InetSocketAddress(LOOPBACK_HOST, OAUTH_REDIRECT_PORT),
                            SINGLE_CONNECTION_BACKLOG,
                        )
                    }.getOrElse {
                        finishOnce(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                        return@suspendCancellableCoroutine
                    }
                    server = created
                    created.createContext(OAUTH_REDIRECT_PATH) { exchange ->
                        handleOAuthCallback(
                            exchange = exchange,
                            expectedState = state,
                            codeVerifier = verifier,
                            redirectUri = redirectUri,
                            onFinish = ::finishOnce,
                        )
                    }
                    created.executor = null
                    created.start()
                    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                    if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
                        finishOnce(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                        return@suspendCancellableCoroutine
                    }
                    runCatching {
                        desktop.browse(URI(authUrl))
                    }.onFailure {
                        finishOnce(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                    }
                }
            } ?: Outcome.failure(SessionDomainError.GoogleSignInCancelled)
        }

    private fun handleOAuthCallback(
        exchange: HttpExchange,
        expectedState: String,
        codeVerifier: String,
        redirectUri: String,
        onFinish: (Outcome<SessionDomainError, String>) -> Unit,
    ) {
        runCatching {
            val rawQuery = exchange.requestURI.rawQuery.orEmpty()
            val params = parseQueryParams(rawQuery)
            if (params[QUERY_KEY_ERROR] != null) {
                respondHtml(exchange, OAUTH_HTML_CLOSE_BODY)
                bringDesktopAppToForeground()
                onFinish(Outcome.failure(SessionDomainError.GoogleSignInCancelled))
                return@runCatching
            }
            val code = params[QUERY_KEY_CODE]
            val state = params[QUERY_KEY_STATE]
            if (code.isNullOrBlank() || state != expectedState) {
                respondHtml(exchange, OAUTH_HTML_ERROR_BODY)
                bringDesktopAppToForeground()
                onFinish(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                return@runCatching
            }
            val tokenOutcome = exchangeCodeForIdToken(
                code = code,
                codeVerifier = codeVerifier,
                redirectUri = redirectUri,
            )
            if (tokenOutcome is Outcome.Success) {
                respondHtml(exchange, OAUTH_HTML_SUCCESS_BODY)
                bringDesktopAppToForeground()
                onFinish(Outcome.success(tokenOutcome.value))
            } else {
                respondHtml(exchange, OAUTH_HTML_ERROR_BODY)
                bringDesktopAppToForeground()
                onFinish(tokenOutcome)
            }
        }.onFailure {
            runCatching {
                respondHtml(exchange, OAUTH_HTML_ERROR_BODY)
            }
            bringDesktopAppToForeground()
            onFinish(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
        }
    }

    private fun respondHtml(
        exchange: HttpExchange,
        body: String,
    ) {
        val bytes = body.toByteArray(headwayUtf8Charset)
        exchange.responseHeaders.set(HEADER_CONTENT_TYPE, CONTENT_TYPE_HTML_UTF8)
        exchange.sendResponseHeaders(HTTP_OK, bytes.size.toLong())
        exchange.responseBody.use { stream ->
            stream.write(bytes)
        }
    }

    private fun exchangeCodeForIdToken(
        code: String,
        codeVerifier: String,
        redirectUri: String,
    ): Outcome<SessionDomainError, String> = runCatching {
        val connection = URI(TOKEN_ENDPOINT).toURL().openConnection() as HttpURLConnection
        connection.requestMethod = HTTP_METHOD_POST
        connection.doOutput = true
        connection.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM_URLENCODED)
        val formPairs = buildList {
            add(QUERY_KEY_CODE to code)
            add(QUERY_KEY_CLIENT_ID to GOOGLE_WEB_CLIENT_ID)
            add(QUERY_KEY_REDIRECT_URI to redirectUri)
            add(QUERY_KEY_GRANT_TYPE to GRANT_TYPE_AUTHORIZATION_CODE)
            add(QUERY_KEY_CODE_VERIFIER to codeVerifier)
            val secret = googleOAuthClientSecret?.trim().orEmpty()
            if (secret.isNotEmpty()) {
                add(QUERY_KEY_CLIENT_SECRET to secret)
            }
        }
        val body = formPairs.joinToString(separator = QUERY_PAIR_SEPARATOR) { (key, value) ->
            "${URLEncoder.encode(key, headwayUtf8Charset)}" +
                "${KEY_VALUE_SEPARATOR}${URLEncoder.encode(value, headwayUtf8Charset)}"
        }
        connection.outputStream.use { stream ->
            stream.write(body.toByteArray(headwayUtf8Charset))
        }
        val status = connection.responseCode
        val payloadStream = if (status in HTTP_STATUS_SUCCESS_MIN..HTTP_STATUS_SUCCESS_MAX) {
            connection.inputStream
        } else {
            connection.errorStream ?: connection.inputStream
        }
        val responseText = payloadStream.use { stream ->
            stream.readBytes().decodeToString()
        }
        val root = Json.parseToJsonElement(responseText) as? JsonObject
        if (root == null) {
            return Outcome.failure(SessionDomainError.GoogleSignInUnavailable)
        }
        val idToken = root[JSON_KEY_ID_TOKEN]?.jsonPrimitive?.content
        if (idToken.isNullOrBlank()) {
            Outcome.failure(SessionDomainError.GoogleSignInUnavailable)
        } else {
            Outcome.success(idToken)
        }
    }.getOrElse { Outcome.failure(SessionDomainError.GoogleSignInUnavailable) }

    private fun buildAuthorizationUrl(
        clientId: String,
        redirectUri: String,
        codeChallenge: String,
        state: String,
    ): String {
        val enc = headwayUtf8Charset
        return "$AUTH_ENDPOINT?" +
            "${QUERY_KEY_CLIENT_ID}=${URLEncoder.encode(clientId, enc)}&" +
            "${QUERY_KEY_REDIRECT_URI}=${URLEncoder.encode(redirectUri, enc)}&" +
            "${QUERY_KEY_RESPONSE_TYPE}=$RESPONSE_TYPE_CODE&" +
            "${QUERY_KEY_SCOPE}=${URLEncoder.encode(OAUTH_SCOPES, enc)}&" +
            "${QUERY_KEY_CODE_CHALLENGE}=${URLEncoder.encode(codeChallenge, enc)}&" +
            "${QUERY_KEY_CODE_CHALLENGE_METHOD}=$CHALLENGE_METHOD_S256&" +
            "${QUERY_KEY_STATE}=${URLEncoder.encode(state, enc)}&" +
            "${QUERY_KEY_PROMPT}=$PROMPT_SELECT_ACCOUNT"
    }

    private fun parseQueryParams(rawQuery: String): Map<String, String> {
        if (rawQuery.isEmpty()) {
            return emptyMap()
        }
        return rawQuery.split(QUERY_PAIR_SEPARATOR).asSequence()
            .mapNotNull { pair ->
                val idx = pair.indexOf(KEY_VALUE_SEPARATOR)
                if (idx <= 0) {
                    null
                } else {
                    val key = URLDecoder.decode(
                        pair.substring(0, idx),
                        headwayUtf8Charset,
                    )
                    val value = URLDecoder.decode(
                        pair.substring(idx + 1),
                        headwayUtf8Charset,
                    )
                    key to value
                }
            }
            .toMap()
    }

    private fun generateCodeVerifier(): String = randomUrlSafe(CODE_VERIFIER_BYTE_LENGTH)

    private fun randomUrlSafe(byteLength: Int): String {
        val random = SecureRandom()
        val bytes = ByteArray(byteLength)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    private fun codeChallengeS256(verifier: String): String {
        val digest = MessageDigest.getInstance(DIGEST_SHA256).digest(verifier.toByteArray(StandardCharsets.US_ASCII))
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest)
    }

    private fun bringDesktopAppToForeground() {
        EventQueue.invokeLater {
            runCatching {
                val applicationClass = Class.forName("com.apple.eawt.Application")
                val application = applicationClass.getMethod("getApplication").invoke(null)
                applicationClass.getMethod("requestForeground", Boolean::class.javaPrimitiveType)
                    .invoke(application, true)
            }
            for (frame in Frame.getFrames()) {
                if (frame.isDisplayable && frame.isVisible) {
                    if (frame.extendedState and Frame.ICONIFIED != 0) {
                        frame.extendedState = Frame.NORMAL
                    }
                    frame.toFront()
                    frame.requestFocus()
                }
            }
        }
    }
}

private const val GOOGLE_WEB_CLIENT_ID: String =
    "658272377808-alf63nc9km4tjgv0dr6lltfqfo1jshqb.apps.googleusercontent.com"

private const val LOOPBACK_HOST: String = "127.0.0.1"

private const val OAUTH_REDIRECT_PORT: Int = 8405

private const val OAUTH_REDIRECT_PATH: String = "/oauth2callback"

private const val OAUTH_REDIRECT_URI: String = "http://127.0.0.1:8405/oauth2callback"

private const val AUTH_ENDPOINT: String = "https://accounts.google.com/o/oauth2/v2/auth"

private const val TOKEN_ENDPOINT: String = "https://oauth2.googleapis.com/token"

private const val OAUTH_SCOPES: String = "openid email profile"

private const val OAUTH_CODE_VALUE: String = "code"

private const val RESPONSE_TYPE_CODE: String = OAUTH_CODE_VALUE

private const val CHALLENGE_METHOD_S256: String = "S256"

private const val GRANT_TYPE_AUTHORIZATION_CODE: String = "authorization_code"

private const val PROMPT_SELECT_ACCOUNT: String = "select_account"

private const val QUERY_KEY_CODE: String = OAUTH_CODE_VALUE

private const val QUERY_KEY_STATE: String = "state"

private const val QUERY_KEY_ERROR: String = "error"

private const val QUERY_KEY_CLIENT_ID: String = "client_id"

private const val QUERY_KEY_REDIRECT_URI: String = "redirect_uri"

private const val QUERY_KEY_RESPONSE_TYPE: String = "response_type"

private const val QUERY_KEY_SCOPE: String = "scope"

private const val QUERY_KEY_CODE_CHALLENGE: String = "code_challenge"

private const val QUERY_KEY_CODE_CHALLENGE_METHOD: String = "code_challenge_method"

private const val QUERY_KEY_PROMPT: String = "prompt"

private const val QUERY_KEY_GRANT_TYPE: String = "grant_type"

private const val QUERY_KEY_CODE_VERIFIER: String = "code_verifier"

private const val QUERY_KEY_CLIENT_SECRET: String = "client_secret"

private const val JSON_KEY_ID_TOKEN: String = "id_token"

private const val HEADER_CONTENT_TYPE: String = "Content-Type"

private const val CONTENT_TYPE_HTML_UTF8: String = "text/html; charset=UTF-8"

private const val CONTENT_TYPE_FORM_URLENCODED: String = "application/x-www-form-urlencoded"

private const val HTTP_OK: Int = 200

private const val HTTP_STATUS_SUCCESS_MIN: Int = 200

private const val HTTP_STATUS_SUCCESS_MAX: Int = 299

private const val HTTP_METHOD_POST: String = "POST"

private const val QUERY_PAIR_SEPARATOR: String = "&"

private const val KEY_VALUE_SEPARATOR: Char = '='

private const val OAUTH_STATE_BYTE_LENGTH: Int = 32

private const val CODE_VERIFIER_BYTE_LENGTH: Int = 32

private const val SINGLE_CONNECTION_BACKLOG: Int = 1

private const val SERVER_STOP_DELAY_SECONDS: Int = 0

private const val OAUTH_WAIT_TIMEOUT_MILLIS: Long = 300_000L

private const val DIGEST_SHA256: String = "SHA-256"

private const val OAUTH_HTML_DOC_PREFIX: String = "<!DOCTYPE html><html><body><p>"

private const val OAUTH_HTML_DOC_SUFFIX: String = "</p></body></html>"

private const val OAUTH_HTML_SUCCESS_BODY: String =
    "${OAUTH_HTML_DOC_PREFIX}Sign-in complete. You can close this tab.$OAUTH_HTML_DOC_SUFFIX"

private const val OAUTH_HTML_ERROR_BODY: String =
    "${OAUTH_HTML_DOC_PREFIX}Sign-in failed. You can close this tab.$OAUTH_HTML_DOC_SUFFIX"

private const val OAUTH_HTML_CLOSE_BODY: String =
    "${OAUTH_HTML_DOC_PREFIX}You can close this tab.$OAUTH_HTML_DOC_SUFFIX"

private val headwayUtf8Charset: Charset = StandardCharsets.UTF_8
