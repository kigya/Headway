package dev.kigya.headway.gateway.presentation

import com.apurebase.kgraphql.GraphQL
import com.apurebase.kgraphql.GraphQLError
import dev.kigya.headway.gateway.core.exception.GatewayErrorCode
import dev.kigya.headway.gateway.core.presentation.GatewayGraphqlErrorMapper
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.graphql.parseGatewayAppLocale
import dev.kigya.headway.gateway.graphql.stringScalarLong
import dev.kigya.headway.gateway.graphql.stringScalarUUID
import dev.kigya.headway.gateway.model.GatewayGoogleLoginResponse
import dev.kigya.headway.gateway.model.GatewayGuestLoginResponse
import dev.kigya.headway.gateway.model.GatewayRefreshAccessTokenResponse
import dev.kigya.headway.gateway.model.GatewaySessionPlatform
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.presentation.routes.GatewayHttpRoute
import dev.kigya.headway.gateway.presentation.schema.authSchema
import dev.kigya.headway.gateway.presentation.schema.databaseSchema
import dev.kigya.headway.gateway.presentation.schema.healthSchema
import dev.kigya.headway.gateway.presentation.schema.homeSchema
import dev.kigya.headway.gateway.presentation.schema.learningQuestionsSchema
import dev.kigya.headway.gateway.presentation.schema.preparationSchema
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log

internal fun Application.installGatewayApi(bindings: GatewayApiBindings) {
    install(GraphQL) {
        playground = !bindings.environment.isProd
        endpoint = GatewayHttpRoute.GraphQL.path
        context { call ->
            +GraphqlRequestContext(
                authorizationHeader = call.request.headers[HttpHeaders.Authorization],
                appLocale = parseGatewayAppLocale(
                    xHeadwayLocale = call.request.headers[X_HEADWAY_LOCALE_HEADER],
                    acceptLanguage = call.request.headers[HttpHeaders.AcceptLanguage],
                ),
            )
        }

        errorHandler { throwable ->
            val envelope = GatewayGraphqlErrorMapper.map(throwable)
            val originalThrowable = GatewayGraphqlErrorMapper.unwrapForLogging(throwable)
            val isSevere = envelope.code == GatewayErrorCode.INTERNAL ||
                envelope.code == GatewayErrorCode.DEPENDENCY_UNAVAILABLE
            val logBlock = """
                |GraphQL error:
                | code=${envelope.code}
                | type=${originalThrowable::class.qualifiedName}
                | msg=${originalThrowable.message}
            """.trimMargin()
            if (isSevere) {
                this@installGatewayApi.log.error(logBlock, originalThrowable)
            } else {
                this@installGatewayApi.log.info(logBlock)
            }

            GraphQLError(
                message = envelope.message,
                originalError = throwable,
                extensions = envelope.extensions,
            )
        }

        schema {
            enum<GatewayUserRole>()
            enum<GatewaySessionPlatform>()
            type<GatewayUser>()
            type<GatewayGoogleLoginResponse>()
            type<GatewayRefreshAccessTokenResponse>()
            type<GatewayGuestLoginResponse>()

            stringScalarUUID()
            stringScalarLong()

            healthSchema(bindings.checkHealthStatus)
            authSchema(
                loginWithGoogle = bindings.loginWithGoogle,
                loginAsGuestUseCase = bindings.loginAsGuest,
                refreshAccessToken = bindings.refreshToken,
            )
            databaseSchema(
                inviteUser = bindings.inviteUser,
                resolvePrincipal = bindings.resolvePrincipal,
            )
            homeSchema(
                resolvePrincipal = bindings.resolvePrincipal,
                getHomeScreen = bindings.getHomeScreen,
            )
            preparationSchema(
                resolvePrincipal = bindings.resolvePrincipal,
                preparation = bindings.preparation,
            )
            learningQuestionsSchema(
                resolvePrincipal = bindings.resolvePrincipal,
                learningQuestions = bindings.learningQuestions,
            )
        }
    }
}

private const val X_HEADWAY_LOCALE_HEADER: String = "X-Headway-Locale"
