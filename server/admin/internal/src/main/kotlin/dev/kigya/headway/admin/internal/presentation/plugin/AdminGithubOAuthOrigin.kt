package dev.kigya.headway.admin.internal.presentation.plugin

import dev.kigya.headway.admin.api.url.adminServiceUrlHolder
import io.ktor.server.application.ApplicationCall
import io.ktor.server.plugins.origin
import io.ktor.util.StringValues
import java.net.URI
import java.net.URISyntaxException

internal fun githubCallbackUrl(
    call: ApplicationCall,
    oauthPublicOrigin: String?,
): String {
    val candidates = parsePublicOriginCandidates(oauthPublicOrigin)
    if (candidates.isNotEmpty()) {
        val requestOrigin = buildRequestPublicOriginWithoutPath(call)
        val base = selectPublicOriginBase(
            candidates = candidates,
            requestOrigin = requestOrigin,
        )
        return "$base$githubCallbackPath"
    }
    val headers = call.request.headers
    val forwardedProto = firstHeaderValue(headers, HEADER_X_FORWARDED_PROTO)
    val forwardedHost = firstHeaderValue(headers, HEADER_X_FORWARDED_HOST)
    if (forwardedProto != null && forwardedHost != null) {
        return "$forwardedProto$PROTOCOL_HOST_SEPARATOR$forwardedHost$githubCallbackPath"
    }
    return "${buildOriginFromServer(call)}$githubCallbackPath"
}

internal fun parsePublicOriginCandidates(raw: String?): List<String> {
    val text = raw?.trim().orEmpty()
    if (text.isEmpty()) {
        return emptyList()
    }
    return text.splitToSequence(',')
        .map { segment -> segment.trim().trimEnd('/') }
        .filter { segment -> segment.isNotEmpty() }
        .toList()
}

internal fun buildRequestPublicOriginWithoutPath(call: ApplicationCall): String {
    val headers = call.request.headers
    val forwardedProto = firstHeaderValue(headers, HEADER_X_FORWARDED_PROTO)
    val forwardedHost = firstHeaderValue(headers, HEADER_X_FORWARDED_HOST)
    if (forwardedProto != null && forwardedHost != null) {
        return "$forwardedProto$PROTOCOL_HOST_SEPARATOR$forwardedHost".trimEnd('/')
    }
    return buildOriginFromServer(call)
}

internal fun selectPublicOriginBase(
    candidates: List<String>,
    requestOrigin: String,
): String {
    val cleaned = candidates.asSequence()
        .map { candidate -> candidate.trim().trimEnd('/') }
        .filter { candidate -> candidate.isNotEmpty() }
        .toList()
    val normalizedRequest = normalizeComparableOrigin(requestOrigin)
    val exact = cleaned.find { candidate ->
        normalizeComparableOrigin(candidate) == normalizedRequest
    }
    if (exact != null) {
        return exact
    }
    val loopback = cleaned.find { candidate ->
        isLocalhostLoopbackAliasPair(
            normalizeComparableOrigin(candidate),
            normalizedRequest,
        )
    }
    return loopback ?: cleaned.first()
}

private fun buildOriginFromServer(call: ApplicationCall): String {
    val origin = call.request.origin
    val port = origin.serverPort
    val defaultPort = (origin.scheme == SCHEME_HTTP && port == HTTP_PORT) ||
        (origin.scheme == SCHEME_HTTPS && port == HTTPS_PORT)
    val portSuffix = if (defaultPort) {
        ""
    } else {
        ":$port"
    }
    return "${origin.scheme}$PROTOCOL_HOST_SEPARATOR${origin.serverHost}$portSuffix"
}

private fun firstHeaderValue(
    headers: StringValues,
    name: String,
): String? = headers[name]?.substringBefore(',')?.trim()?.takeIf { value -> value.isNotBlank() }

private fun normalizeComparableOrigin(origin: String): String =
    origin.trim().trimEnd('/').let { trimmed ->
        val uri = try {
            URI(trimmed)
        } catch (_: URISyntaxException) {
            null
        }
        uri?.let { parsed -> comparableHttpHttpsUri(parsed, trimmed) } ?: trimmed.lowercase()
    }

private fun comparableHttpHttpsUri(
    uri: URI,
    fallback: String,
): String {
    val scheme = uri.scheme?.lowercase()
    val host = uri.host?.lowercase()
    if (scheme == null || host == null) {
        return fallback.lowercase()
    }
    if (scheme != SCHEME_HTTP && scheme != SCHEME_HTTPS) {
        return fallback.lowercase()
    }
    val explicitPort = uri.port
    val resolvedPort = when {
        explicitPort != -1 -> explicitPort
        scheme == SCHEME_HTTP -> HTTP_PORT
        else -> HTTPS_PORT
    }
    val isDefaultPort = (scheme == SCHEME_HTTP && resolvedPort == HTTP_PORT) ||
        (scheme == SCHEME_HTTPS && resolvedPort == HTTPS_PORT)
    val portSuffix = if (isDefaultPort) {
        ""
    } else {
        ":$resolvedPort"
    }
    return "$scheme$PROTOCOL_HOST_SEPARATOR$host$portSuffix"
}

private fun isLocalhostLoopbackAliasPair(
    left: String,
    right: String,
): Boolean {
    val leftUri = try {
        URI(left)
    } catch (_: URISyntaxException) {
        null
    }
    val rightUri = try {
        URI(right)
    } catch (_: URISyntaxException) {
        null
    }
    if (leftUri == null || rightUri == null) {
        return false
    }
    val leftScheme = leftUri.scheme?.lowercase()
    val rightScheme = rightUri.scheme?.lowercase()
    if (leftScheme != rightScheme) {
        return false
    }
    val leftHost = leftUri.host?.lowercase()
    val rightHost = rightUri.host?.lowercase()
    val loopbackHosts = setOf("localhost", "127.0.0.1", "[::1]")
    val hostsAreLoopback = leftHost in loopbackHosts && rightHost in loopbackHosts
    return hostsAreLoopback && resolveSchemePort(leftUri) == resolveSchemePort(rightUri)
}

private fun resolveSchemePort(uri: URI): Int {
    if (uri.port != -1) {
        return uri.port
    }
    val scheme = uri.scheme?.lowercase()
    return if (scheme == SCHEME_HTTP) {
        HTTP_PORT
    } else {
        HTTPS_PORT
    }
}

private val githubCallbackPath = "${adminServiceUrlHolder.baseUrl}/auth/github/callback"

private const val HTTP_PORT = 80
private const val HTTPS_PORT = 443
private const val HEADER_X_FORWARDED_PROTO = "X-Forwarded-Proto"
private const val HEADER_X_FORWARDED_HOST = "X-Forwarded-Host"
private const val PROTOCOL_HOST_SEPARATOR = "://"
private const val SCHEME_HTTP = "http"
private const val SCHEME_HTTPS = "https"
