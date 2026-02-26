package dev.kigya.headway.auth.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthRefreshTokenPayloadDto(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)
