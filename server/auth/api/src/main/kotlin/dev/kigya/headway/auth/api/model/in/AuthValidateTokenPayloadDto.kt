package dev.kigya.headway.auth.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthValidateTokenPayloadDto(
    @SerialName("access_token") val accessToken: String,
)
