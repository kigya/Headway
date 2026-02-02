package dev.kigya.headway.gateway.api.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.gateway.api.model.InvitedUserPayload
import dev.kigya.headway.gateway.api.port.InviteUserUseCaseContract

internal fun SchemaBuilder.usersSchema(
    inviteUserUseCaseContract: InviteUserUseCaseContract,
) {
    type<InvitedUserPayload>()

    mutation("inviteUser") {
        description = "Invite user by email and department"
        resolver { email: String, department: String ->
            inviteUserUseCaseContract(email = email, department = department)
        }
    }
}
