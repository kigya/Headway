package dev.kigya.headway.database.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseUpsertGoogleUserPayloadDto(
    @SerialName("google_id") val googleId: String,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("avatar_url") val avatarUrl: String? = null,
)
