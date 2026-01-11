package dev.kigya.headway.database.api.routing.user.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UpsertGoogleUserRequestDto(
    @SerialName("google_id") val googleId: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)
