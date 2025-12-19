package dev.kigya.headway.auth.data.database.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ValidateSessionRequestDto(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)
