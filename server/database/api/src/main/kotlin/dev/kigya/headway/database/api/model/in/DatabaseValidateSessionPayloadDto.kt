package dev.kigya.headway.database.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabaseValidateSessionPayloadDto(
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("fingerprint") val fingerprint: String,
)
