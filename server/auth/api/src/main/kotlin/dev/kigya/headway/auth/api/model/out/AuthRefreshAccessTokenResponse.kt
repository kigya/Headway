package dev.kigya.headway.auth.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRefreshAccessTokenResponse(
    @SerialName("access_token") val accessToken: String,
)
