package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseInviteUserPayloadDto(
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
    @SerialName("role") val role: DatabaseUserRole? = null,
)
