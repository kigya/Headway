package dev.kigya.headway.database.routing.user.request

import dev.kigya.headway.database.domain.model.ExposedUserRole
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateUserRequestDto(
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("google_id") val googleId: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("role") val role: ExposedUserRole,
)
