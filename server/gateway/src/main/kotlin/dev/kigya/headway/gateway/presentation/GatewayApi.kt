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
import dev.kigya.headway.gateway.graphql.stringScalarLong
import dev.kigya.headway.gateway.graphql.stringScalarUUID
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.presentation.routes.GatewayHttpRoute
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
        endpoint = GatewayHttpRoute.GraphQL.path

        errorHandler { throwable ->
            val originalThrowable = unwrapGraphQlError(throwable)

            val (code, message) = when (originalThrowable) {
                is GatewayException -> originalThrowable.code to originalThrowable.code.rawName
                is KtorBadRequestException -> GatewayErrorCode.BAD_REQUEST to (originalThrowable.message
                    ?: "Bad request")

                is IllegalArgumentException -> GatewayErrorCode.BAD_REQUEST to "Bad request"
                else -> GatewayErrorCode.INTERNAL to "Internal server error"
            }

            val extensions = buildExtensions(code = code, throwable = originalThrowable)

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
                originalError = throwable,
                extensions = extensions,
            )
        }

        schema {
            enum<GatewayUserRole>()
            enum<GatewaySessionPlatform>()
            type<GatewayUser>()
            type<GatewayGoogleLoginResponse>()
            type<GatewayRefreshAccessTokenResponse>()

            stringScalarUUID()
            stringScalarLong()

            healthSchema(checkHealthStatus)
            authSchema(loginWithGoogle, refreshToken)
            databaseSchema(inviteUser)
        }
    }
}

private fun buildExtensions(
    code: GatewayErrorCode,
    throwable: Throwable,
): Map<String, Any?> = buildMap {
    put(EXT_CODE, code.name)

    val httpStatus = (throwable as? GatewayException)?.httpStatus ?: when (code) {
        GatewayErrorCode.BAD_REQUEST -> 400
        GatewayErrorCode.UNAUTHORIZED -> 401
        GatewayErrorCode.FORBIDDEN -> 403
        GatewayErrorCode.CONFLICT -> 409
        GatewayErrorCode.DEPENDENCY_UNAVAILABLE -> 503
        GatewayErrorCode.INTERNAL -> 500
        else -> 404
    }
    put(EXT_HTTP_STATUS, httpStatus)

    when (throwable) {
        is GatewayException.DependencyUnavailable -> {
            put(EXT_DEPENDENCY, throwable.dependency)
        }

        is GatewayException.UpstreamProtocol -> {
            put(EXT_DEPENDENCY, throwable.dependency)
            put(EXT_UPSTREAM_STATUS, throwable.status)
        }
    }
}

private fun unwrapGraphQlError(t: Throwable): Throwable {
    val exception = t as? ExecutionException
    return exception?.originalError ?: t
}

private const val EXT_CODE = "code"
private const val EXT_HTTP_STATUS = "httpStatus"
private const val EXT_DEPENDENCY = "dependency"
private const val EXT_UPSTREAM_STATUS = "upstreamStatus"
