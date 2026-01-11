package dev.kigya.headway.api

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.api.graphql.stringScalarLong
import dev.kigya.headway.api.graphql.stringScalarUUID
import dev.kigya.headway.api.port.CheckHealthStatusUseCaseContract
import dev.kigya.headway.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.api.schema.authSchema
import dev.kigya.headway.api.schema.healthSchema
import exception.BadRequestException
import exception.ForbiddenException
import exception.InternalServerException
import exception.UserNotExistsException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log

fun Application.installGatewayApi(
    checkHealthStatusUseCaseContract: CheckHealthStatusUseCaseContract,
    loginWithGoogleUseCaseContract: LoginWithGoogleUseCaseContract,
    refreshTokenUseCaseContract: RefreshTokenUseCaseContract,
) {
    install(GraphQL) {
        playground = true
        endpoint = "/api/v1/graphql"

        errorHandler { throwable ->
            val original = unwrapGraphQlError(throwable)

            val (code, message) = when (original) {
                is BadRequestException ->
                    ErrorCode.BAD_REQUEST to (original.error.message.ifBlank { "Bad request" })

                is ForbiddenException ->
                    ErrorCode.FORBIDDEN to (original.error.message.ifBlank { "Forbidden" })

                is UserNotExistsException ->
                    ErrorCode.USER_NOT_FOUND to (original.error.message.ifBlank { "User not found" })

                is InternalServerException ->
                    ErrorCode.INTERNAL to "Internal server error"

                else ->
                    ErrorCode.INTERNAL to "Internal server error"
            }

            if (code == ErrorCode.INTERNAL) {
                this@installGatewayApi.log.error(
                    "GraphQL error: code=$code, type=${original::class.qualifiedName}, msg=${original.message}"
                )
            } else {
                this@installGatewayApi.log.info(
                    "GraphQL error: code=$code, type=${original::class.simpleName}, msg=${original.message}"
                )
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
    FORBIDDEN,
    USER_NOT_FOUND,
    INTERNAL,
}

private fun unwrapGraphQlError(t: Throwable): Throwable {
    val exception = t as? ExecutionException
    return exception?.originalError ?: t
}
