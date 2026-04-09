package dev.kigya.headway.core.session.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface LocalSessionRecord {

    @Serializable
    @SerialName("registered")
    data class Registered(
        @SerialName("accessToken") val accessToken: String,
        @SerialName("refreshToken") val refreshToken: String,
        @SerialName("userId") val userId: String,
        @SerialName("userEmail") val userEmail: String,
        @SerialName("userName") val userName: String,
    ) : LocalSessionRecord

    @Serializable
    @SerialName("guest")
    data class Guest(
        @SerialName("accessToken") val accessToken: String,
        @SerialName("expiresAtEpochMs") val expiresAtEpochMs: Long,
    ) : LocalSessionRecord
}
