package dev.kigya.headway.gateway

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.common.exception.BadRequestException
import dev.kigya.headway.common.exception.ForbiddenException
import dev.kigya.headway.common.exception.InternalServerException
import dev.kigya.headway.gateway.graphql.stringScalarLong
import dev.kigya.headway.gateway.graphql.stringScalarUUID
import dev.kigya.headway.gateway.port.CheckHealthStatusUseCaseContract
import dev.kigya.headway.gateway.port.InviteUserUseCaseContract
import dev.kigya.headway.gateway.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.gateway.port.RefreshTokenUseCaseContract
import dev.kigya.headway.gateway.schema.authSchema
import dev.kigya.headway.gateway.schema.healthSchema
import dev.kigya.headway.gateway.schema.databaseSchema
import dev.kigya.headway.gateway.exception.AuthServiceException
import dev.kigya.headway.gateway.exception.DatabaseServiceException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException
import dev.kigya.headway.gateway.exception.GatewayErrorCode as DownstreamCode

fun Application.installGatewayApi(
    checkHealthStatusUseCaseContract: CheckHealthStatusUseCaseContract,
    loginWithGoogleUseCaseContract: LoginWithGoogleUseCaseContract,
    refreshTokenUseCaseContract: RefreshTokenUseCaseContract,
    inviteUserUseCaseContract: InviteUserUseCaseContract,
) {
    install(GraphQL) {
        playground = true
        endpoint = "/api/v1/graphql"

        errorHandler { throwable ->
            val original = unwrapGraphQlError(throwable)

            val (code, message) = when (original) {
                is AuthServiceException -> mapDownstream(original.code)
                is DatabaseServiceException -> mapDownstream(original.code)

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
            databaseSchema(
                inviteUserUseCaseContract,
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

private fun mapDownstream(code: DownstreamCode): Pair<ErrorCode, String> =
    when (code) {
        DownstreamCode.BAD_REQUEST -> ErrorCode.BAD_REQUEST to "Bad request"
        DownstreamCode.UNAUTHORIZED -> ErrorCode.UNAUTHORIZED to "Unauthorized"
        DownstreamCode.FORBIDDEN -> ErrorCode.FORBIDDEN to "Forbidden"
        DownstreamCode.DEPENDENCY_UNAVAILABLE -> ErrorCode.DEPENDENCY_UNAVAILABLE to "Service temporarily unavailable"
        DownstreamCode.INTERNAL -> ErrorCode.INTERNAL to "Internal server error"
    }

private fun unwrapGraphQlError(t: Throwable): Throwable {
    val exception = t as? ExecutionException
    return exception?.originalError ?: t
}
