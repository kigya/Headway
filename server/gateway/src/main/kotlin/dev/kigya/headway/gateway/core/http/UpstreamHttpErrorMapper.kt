package dev.kigya.headway.gateway.core.http

import dev.kigya.headway.gateway.core.exception.GatewayException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode

internal suspend fun HttpResponse.toGatewayException(dependency: String): GatewayException {
    val bodyMsg = safeBodyMessage()

    return when (status) {
        HttpStatusCode.BadRequest ->
            GatewayException.InvalidRequest(bodyMsg ?: "Bad request")

        HttpStatusCode.Unauthorized ->
            GatewayException.Unauthorized(bodyMsg ?: "Unauthorized")

        HttpStatusCode.Forbidden ->
            GatewayException.Forbidden(bodyMsg ?: "Forbidden")

        HttpStatusCode.NotFound ->
            GatewayException.NotFound(bodyMsg ?: "Not found")

        HttpStatusCode.Conflict ->
            GatewayException.Conflict(bodyMsg ?: "Conflict")

        HttpStatusCode.ServiceUnavailable ->
            GatewayException.DependencyUnavailable(
                dependency = dependency,
                message = "Service temporarily unavailable",
            )

        else ->
            GatewayException.UpstreamProtocol(
                dependency = dependency,
                status = status.value,
                message = "Service temporarily unavailable",
            )
    }
}

private suspend fun HttpResponse.safeBodyMessage(): String? {
    val raw = runCatching { bodyAsText() }.getOrNull()?.trim().orEmpty()
    if (raw.isBlank()) return null
    return raw
        .replace(Regex("\\s+"), " ")
        .take(MAX_ERROR_BODY_LEN)
}

private const val MAX_ERROR_BODY_LEN = 2048
