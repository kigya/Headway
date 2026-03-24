package dev.kigya.headway.auth.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthGuestLoginResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("expires_at_epoch_ms")
    val expiresAtEpochMs: Long,
)
