package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.core.exception.GatewayException
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolveCallerUseCase
import dev.kigya.headway.gateway.graphql.GraphqlRequestContext
import dev.kigya.headway.gateway.model.GatewayUserRole
import dev.kigya.headway.gateway.presentation.routes.GatewayGraphqlOperation

internal fun SchemaBuilder.databaseSchema(
    inviteUser: InviteUserUseCase,
    resolveCaller: ResolveCallerUseCase,
) {
    mutation(GatewayGraphqlOperation.InviteUser.name) {
        description = "Invite user by email and department"
        resolver { email: String, department: String, ctx: Context ->
            val requestContext = ctx.get<GraphqlRequestContext>()
                ?: throw GatewayException.Internal("Missing GraphQL request context")
            val caller = resolveCaller(requestContext.authorizationHeader)
            if (caller.role != GatewayUserRole.DEVELOPER && caller.role != GatewayUserRole.MANAGER) {
                throw GatewayException.Forbidden("Forbidden")
            }

            inviteUser(email = email, department = department)
        }
    }
}
