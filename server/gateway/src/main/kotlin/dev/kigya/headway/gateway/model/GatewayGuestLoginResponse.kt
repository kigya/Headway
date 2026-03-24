package dev.kigya.headway.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GatewayGuestLoginResponse(
    @SerialName("accessToken")
    val accessToken: String,
    @SerialName("expiresAtEpochMs")
    val expiresAtEpochMs: Long,
)
