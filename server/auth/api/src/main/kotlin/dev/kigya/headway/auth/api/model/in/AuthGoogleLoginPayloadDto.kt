package dev.kigya.headway.auth.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthGoogleLoginPayloadDto(
    @SerialName("id_token") val idToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)
