package dev.kigya.headway.gateway.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.model.GatewayUser
import dev.kigya.headway.gateway.port.InviteUserUseCaseContract

internal fun SchemaBuilder.databaseSchema(
    inviteUserUseCaseContract: InviteUserUseCaseContract,
) {
    type<GatewayUser>()

    mutation("inviteUser") {
        description = "Invite user by email and department"
        resolver { email: String, department: String ->
            inviteUserUseCaseContract(email = email, department = department)
        }
    }
}
