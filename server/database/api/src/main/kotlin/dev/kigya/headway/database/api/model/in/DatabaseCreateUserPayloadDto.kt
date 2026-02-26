package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseCreateUserPayloadDto(
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("google_id") val googleId: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("role") val role: DatabaseUserRole,
)
