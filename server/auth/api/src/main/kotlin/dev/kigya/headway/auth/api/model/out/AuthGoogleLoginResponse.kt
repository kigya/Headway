package dev.kigya.headway.auth.api.model.out

import dev.kigya.headway.database.api.model.out.DatabaseUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthGoogleLoginResponse(
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("user") val user: DatabaseUser,
)
