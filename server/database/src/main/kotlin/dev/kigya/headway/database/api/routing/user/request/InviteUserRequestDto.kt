package dev.kigya.headway.database.api.routing.user.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class InviteUserRequestDto(
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
)
