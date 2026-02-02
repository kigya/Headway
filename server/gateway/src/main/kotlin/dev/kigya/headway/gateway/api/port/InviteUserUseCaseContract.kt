package dev.kigya.headway.gateway.api.port

import dev.kigya.headway.gateway.api.model.InvitedUserPayload

interface InviteUserUseCaseContract {
    suspend operator fun invoke(
        email: String,
        department: String,
    ): InvitedUserPayload
}
