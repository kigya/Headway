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

    init {
        DesktopOAuthLoopbackServer.configureShutdownDispatcher(ioDispatcher)
        DesktopOAuthAbandonmentMonitor.configureMonitorDispatcher(ioDispatcher)
    }

    override suspend fun obtainIdToken(): Outcome<SessionDomainError, String> =
        withContext(ioDispatcher) {
            withTimeoutOrNull(OAUTH_WAIT_TIMEOUT_MILLIS) {
                suspendCancellableCoroutine<Outcome<SessionDomainError, String>> { continuation ->
                    val authFinished = AtomicBoolean(false)
                    fun stopServer(server: HttpServer?) {
                        if (server != null) {
                            DesktopOAuthLoopbackServer.releaseIfActive(server)
                        } else {
                            DesktopOAuthLoopbackServer.release()
                        }
                    }
                    fun finishAuth(outcome: Outcome<SessionDomainError, String>) {
                        if (!authFinished.compareAndSet(false, true)) {
                            return
                        }
                        DesktopOAuthAbandonmentMonitor.endMonitoring()
                        if (continuation.isActive) {
                            continuation.resumeWith(Result.success(outcome))
                        }
                    }
                    fun scheduleServerShutdown(server: HttpServer) {
                        DesktopOAuthLoopbackServer.scheduleReleaseIfActive(
                            server = server,
                            delayMillis = SERVER_KEEP_ALIVE_MILLIS,
                        )
                    }
                    continuation.invokeOnCancellation {
                        DesktopOAuthAbandonmentMonitor.endMonitoring()
                        DesktopOAuthLoopbackServer.release()
                    }
                    DesktopOAuthLoopbackServer.release()
                    val verifier = generateCodeVerifier()
                    val challenge = codeChallengeS256(verifier)
                    val state = randomUrlSafe(OAUTH_STATE_BYTE_LENGTH)
                    val redirectUri = DesktopOAuthLoopbackEndpoint.redirectUri
                    val authUrl = buildAuthorizationUrl(
                        clientId = GOOGLE_WEB_CLIENT_ID,
                        redirectUri = redirectUri,
                        codeChallenge = challenge,
                        state = state,
                    )
                    val created = runCatching {
                        HttpServer.create(
                            InetSocketAddress(
                                DesktopOAuthLoopbackEndpoint.HOST,
                                DesktopOAuthLoopbackEndpoint.PORT,
                            ),
                            SINGLE_CONNECTION_BACKLOG,
                        )
                    }.getOrElse {
                        finishAuth(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                        return@suspendCancellableCoroutine
                    }
                    DesktopOAuthLoopbackServer.register(created)
                    created.createContext(DesktopOAuthLoopbackEndpoint.CALLBACK_PATH) { exchange ->
                        handleOAuthCallback(
                            OAuthCallbackContext(
                                exchange = exchange,
                                expectedState = state,
                                codeVerifier = verifier,
                                redirectUri = redirectUri,
                                onFinish = ::finishAuth,
                                onKeepServerAlive = { scheduleServerShutdown(created) },
                            ),
                        )
                    }
                    created.createContext(DesktopOAuthLoopbackEndpoint.OPEN_APP_PATH) { exchange ->
                        DesktopAppForeground.bringToForeground()
                        respondHtml(exchange, buildOAuthReturnedHtml())
                        scheduleServerShutdown(created)
                    }
                    created.executor = null
                    created.start()
                    val desktop = if (Desktop.isDesktopSupported()) Desktop.getDesktop() else null
                    if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
                        finishAuth(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                        stopServer(created)
                        return@suspendCancellableCoroutine
                    }
                    runCatching {
                        desktop.browse(URI(authUrl))
                    }.onSuccess {
                        DesktopOAuthAbandonmentMonitor.beginMonitoring {
                            finishAuth(Outcome.failure(SessionDomainError.GoogleSignInCancelled))
                            stopServer(created)
                        }
                    }.onFailure {
                        finishAuth(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                        stopServer(created)
                    }
                }
            } ?: Outcome.failure(SessionDomainError.GoogleSignInCancelled)
        }

    private fun handleOAuthCallback(context: OAuthCallbackContext) {
        runCatching {
            val rawQuery = context.exchange.requestURI.rawQuery.orEmpty()
            val params = parseQueryParams(rawQuery)
            if (params[QUERY_KEY_ERROR] != null) {
                respondOAuthCallbackPage(
                    context = context,
                    page = OAuthCallbackPage.Cancel,
                    outcome = Outcome.failure(SessionDomainError.GoogleSignInCancelled),
                )
                return@runCatching
            }
            val code = params[QUERY_KEY_CODE]
            val state = params[QUERY_KEY_STATE]
            if (code.isNullOrBlank() || state != context.expectedState) {
                respondOAuthCallbackPage(
                    context = context,
                    page = OAuthCallbackPage.Error,
                    outcome = Outcome.failure(SessionDomainError.GoogleSignInUnavailable),
                )
                return@runCatching
            }
            when (val tokenOutcome = exchangeCodeForIdToken(
                code = code,
                codeVerifier = context.codeVerifier,
                redirectUri = context.redirectUri,
            )) {
                is Outcome.Success -> respondOAuthCallbackPage(
                    context = context,
                    page = OAuthCallbackPage.Success,
                    outcome = Outcome.success(tokenOutcome.value),
                )
                is Outcome.Failure -> respondOAuthCallbackPage(
                    context = context,
                    page = OAuthCallbackPage.Error,
                    outcome = tokenOutcome,
                )
            }
        }.onFailure {
            runCatching {
                respondOAuthCallbackPage(
                    context = context,
                    page = OAuthCallbackPage.Error,
                    outcome = Outcome.failure(SessionDomainError.GoogleSignInUnavailable),
                )
            }.onFailure {
                context.onFinish(Outcome.failure(SessionDomainError.GoogleSignInUnavailable))
                context.onKeepServerAlive()
            }
        }
    }

    private fun respondOAuthCallbackPage(
        context: OAuthCallbackContext,
        page: OAuthCallbackPage,
        outcome: Outcome<SessionDomainError, String>,
    ) {
        respondHtml(
            exchange = context.exchange,
            body = buildOAuthCallbackHtml(
                title = page.title,
                subtitle = page.subtitle,
                buttonLabel = OAUTH_HTML_OPEN_APP_LABEL,
            ),
        )
        context.onFinish(outcome)
        context.onKeepServerAlive()
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
        val urlEncodingCharset = headwayUtf8Charset
        return "$AUTH_ENDPOINT?" +
            "${QUERY_KEY_CLIENT_ID}=${URLEncoder.encode(clientId, urlEncodingCharset)}&" +
            "${QUERY_KEY_REDIRECT_URI}=${URLEncoder.encode(redirectUri, urlEncodingCharset)}&" +
            "${QUERY_KEY_RESPONSE_TYPE}=$RESPONSE_TYPE_CODE&" +
            "${QUERY_KEY_SCOPE}=${URLEncoder.encode(OAUTH_SCOPES, urlEncodingCharset)}&" +
            "${QUERY_KEY_CODE_CHALLENGE}=${URLEncoder.encode(codeChallenge, urlEncodingCharset)}&" +
            "${QUERY_KEY_CODE_CHALLENGE_METHOD}=$CHALLENGE_METHOD_S256&" +
            "${QUERY_KEY_STATE}=${URLEncoder.encode(state, urlEncodingCharset)}&" +
            "${QUERY_KEY_PROMPT}=$PROMPT_SELECT_ACCOUNT"
    }

    private fun parseQueryParams(rawQuery: String): Map<String, String> {
        if (rawQuery.isEmpty()) {
            return emptyMap()
        }
        return rawQuery.split(QUERY_PAIR_SEPARATOR).asSequence()
            .mapNotNull { pair ->
                val separatorIndex = pair.indexOf(KEY_VALUE_SEPARATOR)
                if (separatorIndex <= 0) {
                    null
                } else {
                    val key = URLDecoder.decode(
                        pair.substring(0, separatorIndex),
                        headwayUtf8Charset,
                    )
                    val value = URLDecoder.decode(
                        pair.substring(separatorIndex + 1),
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
}

private enum class OAuthCallbackPage(
    val title: String,
    val subtitle: String,
) {
    Success(
        title = OAUTH_HTML_SUCCESS_TITLE,
        subtitle = OAUTH_HTML_SUCCESS_SUBTITLE,
    ),
    Error(
        title = OAUTH_HTML_ERROR_TITLE,
        subtitle = OAUTH_HTML_ERROR_SUBTITLE,
    ),
    Cancel(
        title = OAUTH_HTML_CANCEL_TITLE,
        subtitle = OAUTH_HTML_CANCEL_SUBTITLE,
    ),
}

private data class OAuthCallbackContext(
    val exchange: HttpExchange,
    val expectedState: String,
    val codeVerifier: String,
    val redirectUri: String,
    val onFinish: (Outcome<SessionDomainError, String>) -> Unit,
    val onKeepServerAlive: () -> Unit,
)

private const val GOOGLE_WEB_CLIENT_ID: String =
    "658272377808-alf63nc9km4tjgv0dr6lltfqfo1jshqb.apps.googleusercontent.com"

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

private const val OAUTH_WAIT_TIMEOUT_MILLIS: Long = 300_000L

private const val SERVER_KEEP_ALIVE_MILLIS: Long = 120_000L

private const val DIGEST_SHA256: String = "SHA-256"

private const val OAUTH_HTML_SUCCESS_TITLE: String = "Sign-in complete"

private const val OAUTH_HTML_SUCCESS_SUBTITLE: String =
    "Return to Headway to continue."

private const val OAUTH_HTML_ERROR_TITLE: String = "Sign-in failed"

private const val OAUTH_HTML_ERROR_SUBTITLE: String =
    "Return to Headway and try again."

private const val OAUTH_HTML_CANCEL_TITLE: String = "Sign-in cancelled"

private const val OAUTH_HTML_CANCEL_SUBTITLE: String =
    "Return to Headway to try again."

private const val OAUTH_HTML_OPEN_APP_LABEL: String = "Open Headway"

private val headwayUtf8Charset: Charset = StandardCharsets.UTF_8
