package dev.kigya.headway.auth.data.database.request

import dev.kigya.headway.auth.domain.model.UserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUserRequestDto(
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("google_id") val googleId: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("role") val role: UserRole,
)
