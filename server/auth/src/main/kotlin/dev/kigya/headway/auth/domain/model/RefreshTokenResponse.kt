package dev.kigya.headway.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponse(
    @SerialName("access_token") val accessToken: String,
)
