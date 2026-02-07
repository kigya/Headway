package dev.kigya.headway.database.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseInviteUserPayloadDto(
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
)
