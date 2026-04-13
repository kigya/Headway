package dev.kigya.headway.gateway.core.http

import dev.kigya.headway.database.api.model.DatabasePreparationErrorCodes
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import org.slf4j.LoggerFactory

private val upstreamHttpErrorLog = LoggerFactory.getLogger("dev.kigya.headway.gateway.UpstreamHttp")

internal suspend fun HttpResponse.toGatewayException(dependency: String): GatewayException {
    val bodyMessage = safeBodyMessage()
    if (bodyMessage == null && status == HttpStatusCode.BadRequest) {
        upstreamHttpErrorLog.warn(
            "Empty error body from dependency={} method={} url={}",
            dependency,
            call.request.method.value,
            call.request.url,
        )
    }

    return toGatewayExceptionForStatus(
        status = status,
        bodyMessage = bodyMessage,
        dependency = dependency,
    )
}

private fun toGatewayExceptionForStatus(
    status: HttpStatusCode,
    bodyMessage: String?,
    dependency: String,
): GatewayException = when (status) {
    HttpStatusCode.BadRequest ->
        GatewayException.InvalidRequest(
            reason = GatewayErrorReason.BAD_REQUEST,
            message = bodyMessage ?: "Bad request",
        )

    HttpStatusCode.Unauthorized ->
        GatewayException.Unauthorized(
            reason = GatewayErrorReason.INVALID_ACCESS_TOKEN,
            message = bodyMessage ?: "Unauthorized",
        )

    HttpStatusCode.Forbidden ->
        GatewayException.Forbidden(
            reason = forbiddenReason(bodyMessage),
            message = bodyMessage ?: "Forbidden",
        )

    HttpStatusCode.NotFound ->
        GatewayException.NotFound(
            message = bodyMessage ?: "Not found",
        )

    HttpStatusCode.Conflict ->
        GatewayException.Conflict(
            message = bodyMessage ?: "Conflict",
            reason = conflictReason(bodyMessage),
        )

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

private fun conflictReason(bodyMsg: String?): GatewayErrorReason = when (bodyMsg?.trim()) {
    DatabasePreparationErrorCodes.SCOPE_SESSION_CLOSED -> GatewayErrorReason.PREPARATION_SCOPE_SESSION_CLOSED
    else -> GatewayErrorReason.IDENTITY_CONFLICT
}

private fun forbiddenReason(bodyMsg: String?): GatewayErrorReason = when (bodyMsg?.trim()) {
    DatabasePreparationErrorCodes.SUMMARY_REQUIRES_COMPLETED_SESSION ->
        GatewayErrorReason.PREPARATION_SUMMARY_REQUIRES_COMPLETED_SESSION
    else -> GatewayErrorReason.INSUFFICIENT_ROLE
}

private suspend fun HttpResponse.safeBodyMessage(): String? {
    val fromBytes = runCatching { body<ByteArray>() }.getOrNull()
    if (fromBytes != null && fromBytes.isNotEmpty()) {
        val decoded = fromBytes.decodeToString().trim()
        if (decoded.isNotEmpty()) {
            return decoded
                .replace(Regex("\\s+"), " ")
                .take(MAX_ERROR_BODY_LEN)
        }
    }
    return null
}

private const val MAX_ERROR_BODY_LEN = 2048
