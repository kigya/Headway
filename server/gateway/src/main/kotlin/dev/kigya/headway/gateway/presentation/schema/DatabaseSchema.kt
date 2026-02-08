package dev.kigya.headway.gateway.presentation.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase

internal fun SchemaBuilder.databaseSchema(
    inviteUser: InviteUserUseCase,
) {
    type<GatewayUser>()

    mutation("inviteUser") {
        description = "Invite user by email and department"
        resolver { email: String, department: String ->
            inviteUser(email = email, department = department)
        }
    }
}
