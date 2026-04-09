package dev.kigya.headway.gateway.core.presentation

import com.apurebase.kgraphql.ExecutionException
import dev.kigya.headway.gateway.core.exception.GatewayErrorCode
import dev.kigya.headway.gateway.core.exception.GatewayErrorReason
import dev.kigya.headway.gateway.core.exception.GatewayException
import io.ktor.server.plugins.BadRequestException as KtorBadRequestException

internal data class GraphQlErrorEnvelope(
    val message: String,
    val code: GatewayErrorCode,
    val extensions: Map<String, Any?>,
)

internal object GatewayGraphqlErrorMapper {

    fun unwrapForLogging(throwable: Throwable): Throwable = unwrapGraphQlError(throwable)

    fun map(throwable: Throwable): GraphQlErrorEnvelope =
        when (val original = unwrapGraphQlError(throwable)) {
            is GatewayException -> GraphQlErrorEnvelope(
                message = original.message ?: "Error",
                code = original.code,
                extensions = extensionsFor(original),
            )

            is KtorBadRequestException -> {
                val badRequestMessage = original.message ?: DEFAULT_BAD_REQUEST
                GraphQlErrorEnvelope(
                    message = badRequestMessage,
                    code = GatewayErrorCode.BAD_REQUEST,
                    extensions = extensionsFor(
                        GatewayException.InvalidRequest(
                            reason = GatewayErrorReason.BAD_REQUEST,
                            message = badRequestMessage,
                            cause = original,
                        ),
                    ),
                )
            }

            is IllegalArgumentException -> {
                val detail = illegalArgumentDetail(original)
                GraphQlErrorEnvelope(
                    message = detail,
                    code = GatewayErrorCode.BAD_REQUEST,
                    extensions = extensionsFor(
                        GatewayException.InvalidRequest(
                            reason = GatewayErrorReason.BAD_REQUEST,
                            message = detail,
                            cause = original,
                        ),
                    ),
                )
            }

            else -> GraphQlErrorEnvelope(
                message = "Internal server error",
                code = GatewayErrorCode.INTERNAL,
                extensions = extensionsFor(
                    GatewayException.Internal(cause = original),
                ),
            )
        }

    fun extensionsFor(exception: GatewayException): Map<String, Any?> = buildMap {
        put(EXT_CODE, exception.code.name)
        put(EXT_CATEGORY, exception.category.name.lowercase())
        put(EXT_REASON, exception.reason.name)
        put(EXT_HTTP_STATUS, exception.httpStatus)
        put(EXT_RETRYABLE, exception.retryable)
        exception.dependency?.let { put(EXT_DEPENDENCY, it) }
        exception.upstreamStatus?.let { put(EXT_UPSTREAM_STATUS, it) }
    }

    private fun unwrapGraphQlError(t: Throwable): Throwable {
        val exception = t as? ExecutionException
        return exception?.originalError ?: t
    }
}

private fun illegalArgumentDetail(exception: IllegalArgumentException): String {
    val fromMessage = exception.message?.trim().orEmpty()
    if (fromMessage.isNotEmpty()) {
        return fromMessage
    }
    return exception::class.simpleName ?: DEFAULT_BAD_REQUEST
}

private const val DEFAULT_BAD_REQUEST = "Bad request"

private const val EXT_CODE = "code"
private const val EXT_CATEGORY = "category"
private const val EXT_REASON = "reason"
private const val EXT_HTTP_STATUS = "httpStatus"
private const val EXT_RETRYABLE = "retryable"
private const val EXT_DEPENDENCY = "dependency"
private const val EXT_UPSTREAM_STATUS = "upstreamStatus"
