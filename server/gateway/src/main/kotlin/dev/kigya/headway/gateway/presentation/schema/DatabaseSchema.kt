package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.auth.GatewayAuthorizationPolicy
import dev.kigya.headway.gateway.domain.auth.GatewayOperation
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolvePrincipalUseCase
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.databaseSchema(
    inviteUser: InviteUserUseCase,
    resolvePrincipal: ResolvePrincipalUseCase,
) {
    mutation(GatewayGraphqlOperation.InviteUser.name) {
        description = "Invite user by email and department"
        resolver { email: String, department: String, ctx: Context ->
            val requestContext = ctx.get<GraphqlRequestContext>()
                ?: throw GatewayException.Internal("Missing GraphQL request context")
            val principal = resolvePrincipal(requestContext.authorizationHeader)
            GatewayAuthorizationPolicy.ensure(principal, GatewayOperation.InviteUser)

            inviteUser(email = email, department = department)
        }
    }
}
