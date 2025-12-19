package dev.kigya.headway.auth.data.database.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class RefreshRequest(
    @SerialName("refresh_token") val refreshToken: String,
)
