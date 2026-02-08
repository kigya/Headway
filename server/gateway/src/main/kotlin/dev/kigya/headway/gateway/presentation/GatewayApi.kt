package dev.kigya.headway.gateway.presentation

import com.apurebase.kgraphql.ExecutionException
import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.gateway.core.exception.GatewayErrorCode
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.presentation.graphql.stringScalarLong
import dev.kigya.headway.gateway.presentation.graphql.stringScalarUUID
import dev.kigya.headway.gateway.presentation.schema.authSchema
import dev.kigya.headway.gateway.presentation.schema.databaseSchema
import dev.kigya.headway.gateway.presentation.schema.healthSchema
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.BadRequestException as KtorBadRequestException

internal fun Application.installGatewayApi(
    checkHealthStatus: CheckHealthStatusUseCase,
    loginWithGoogle: LoginWithGoogleUseCase,
    refreshToken: RefreshAccessTokenUseCase,
    inviteUser: InviteUserUseCase,
) {
    install(GraphQL) {
        playground = true
        endpoint = "/api/v1/graphql"

        errorHandler { throwable ->
            val originalThrowable = unwrapGraphQlError(throwable)

            val (code, message) = when (originalThrowable) {
                is GatewayException -> originalThrowable.code to originalThrowable.toPublicMessage()
                is KtorBadRequestException -> GatewayErrorCode.BAD_REQUEST to (originalThrowable.message ?: "Bad request")
                is IllegalArgumentException -> GatewayErrorCode.BAD_REQUEST to "Bad request"
                else -> GatewayErrorCode.INTERNAL to "Internal server error"
            }

            if (code == GatewayErrorCode.INTERNAL || code == GatewayErrorCode.DEPENDENCY_UNAVAILABLE) {
                this@installGatewayApi.log.error(
                    "GraphQL error: code=$code type=${originalThrowable::class.qualifiedName} msg=${originalThrowable.message}",
                    originalThrowable,
                )
            } else {
                this@installGatewayApi.log.info("GraphQL error: code=$code type=${originalThrowable::class.simpleName} msg=${originalThrowable.message}")
            }

            GraphQLError(
                message = message,
                extensions = mapOf(EXT_CODE to code.name),
            )
        }

        schema {
            stringScalarUUID()
            stringScalarLong()

            healthSchema(checkHealthStatus)
            authSchema(loginWithGoogle, refreshToken)
            databaseSchema(inviteUser)
        }
    }
}

private const val EXT_CODE = "code"

private fun GatewayException.toPublicMessage(): String =
    when (code) {
        GatewayErrorCode.BAD_REQUEST -> message
        GatewayErrorCode.UNAUTHORIZED -> "Unauthorized"
        GatewayErrorCode.FORBIDDEN -> "Forbidden"
        GatewayErrorCode.DEPENDENCY_UNAVAILABLE -> "Service temporarily unavailable"
        GatewayErrorCode.INTERNAL -> "Internal server error"
    }

private fun unwrapGraphQlError(t: Throwable): Throwable {
    val exception = t as? ExecutionException
    return exception?.originalError ?: t
}
