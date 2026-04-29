package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.auth.GatewayAuthorizationPolicy
import dev.kigya.headway.gateway.domain.auth.GatewayOperation
import dev.kigya.headway.gateway.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.model.GatewayPrincipal
import dev.kigya.headway.gateway.model.HomeScreenNextInterviewType
import dev.kigya.headway.gateway.model.HomeScreenPayload
import dev.kigya.headway.gateway.model.HomeScreenSectionId
import dev.kigya.headway.gateway.model.HomeScreenSectionItem
import dev.kigya.headway.gateway.model.HomeScreenSectionStyle
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.homeSchema(
    resolvePrincipal: ResolvePrincipalUseCase,
    loadHomeScreen: GetHomeScreenUseCase,
) {
    enum<HomeScreenSectionId>()
    enum<HomeScreenSectionStyle>()
    enum<HomeScreenNextInterviewType>()
    type<HomeScreenSectionItem>()
    type<HomeScreenPayload>()

    query(GatewayGraphqlOperation.HomeScreen.name) {
        description = "Home screen payload; locale from X-Headway-Locale or Accept-Language"
        resolver { ctx: Context ->
            val requestContext = ctx.get<GraphqlRequestContext>()
                ?: throw GatewayException.Internal("Missing GraphQL request context")
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.HomeScreen)
            val user = when (principal) {
                is GatewayPrincipal.User -> principal.user
                is GatewayPrincipal.Guest ->
                    throw GatewayException.Internal("Unexpected guest principal")
            }
            loadHomeScreen(
                user = user,
                locale = requestContext.appLocale,
            )
        }
    }
}
