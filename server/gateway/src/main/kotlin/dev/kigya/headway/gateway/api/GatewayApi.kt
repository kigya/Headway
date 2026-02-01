package dev.kigya.headway.gateway.api

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.gateway.api.graphql.stringScalarLong
import dev.kigya.headway.gateway.api.graphql.stringScalarUUID
import dev.kigya.headway.gateway.api.schema.authSchema
import dev.kigya.headway.gateway.api.schema.healthSchema
import dev.kigya.headway.gateway.internal.exception.AuthServiceException
import dev.kigya.headway.common.exception.BadRequestException
import dev.kigya.headway.common.exception.ForbiddenException
import dev.kigya.headway.common.exception.InternalServerException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import dev.kigya.headway.gateway.internal.exception.GatewayErrorCode as DownstreamCode

fun Application.installGatewayApi(
    checkHealthStatusUseCaseContract: dev.kigya.headway.gateway.api.port.CheckHealthStatusUseCaseContract,
    loginWithGoogleUseCaseContract: dev.kigya.headway.gateway.api.port.LoginWithGoogleUseCaseContract,
    refreshTokenUseCaseContract: dev.kigya.headway.gateway.api.port.RefreshTokenUseCaseContract,
) {
    install(GraphQL) {
        playground = true
        endpoint = "/api/v1/graphql"

        errorHandler { throwable ->
            val original = unwrapGraphQlError(throwable)

            val (code, message) = when (original) {
                is AuthServiceException -> when (original.code) {
                    DownstreamCode.BAD_REQUEST -> ErrorCode.BAD_REQUEST to "Bad request"
                    DownstreamCode.UNAUTHORIZED -> ErrorCode.UNAUTHORIZED to "Unauthorized"
                    DownstreamCode.FORBIDDEN -> ErrorCode.FORBIDDEN to "Forbidden"
                    DownstreamCode.DEPENDENCY_UNAVAILABLE -> ErrorCode.DEPENDENCY_UNAVAILABLE to "Service temporarily unavailable"
                    DownstreamCode.INTERNAL -> ErrorCode.INTERNAL to "Internal server error"
                }

                is BadRequestException -> ErrorCode.BAD_REQUEST to "Bad request"
                is ForbiddenException -> ErrorCode.FORBIDDEN to "Forbidden"
                is InternalServerException -> ErrorCode.INTERNAL to "Internal server error"

                else -> ErrorCode.INTERNAL to "Internal server error"
            }

            if (code == ErrorCode.INTERNAL) {
                this@installGatewayApi.log.error("GraphQL error: code=$code type=${original::class.qualifiedName} msg=${original.message}")
            } else {
                this@installGatewayApi.log.info("GraphQL error: code=$code type=${original::class.simpleName} msg=${original.message}")
            }

            GraphQLError(
                message = message,
                extensions = mapOf(EXT_CODE to code.name),
            )
        }

        schema {
            stringScalarUUID()
            stringScalarLong()
            healthSchema(checkHealthStatusUseCaseContract)
            authSchema(
                loginWithGoogleUseCaseContract,
                refreshTokenUseCaseContract,
            )
        }
    }
}

private const val EXT_CODE = "code"

private enum class ErrorCode {
    BAD_REQUEST,
    UNAUTHORIZED,
    FORBIDDEN,
    DEPENDENCY_UNAVAILABLE,
    INTERNAL,
}

private fun unwrapGraphQlError(t: Throwable): Throwable {
    val exception = t as? ExecutionException
    return exception?.originalError ?: t
}
