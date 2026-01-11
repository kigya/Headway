package dev.kigya.headway.auth.internal.client.dto

import dev.kigya.headway.auth.api.serialization.UUIDSerializer
import dev.kigya.headway.auth.internal.serialization.OffsetDateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
internal data class CreateSessionRequestDto(
    @SerialName("user_id") @Serializable(UUIDSerializer::class) val userId: UUID,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") @Serializable(OffsetDateTimeSerializer::class) val expiresIn: OffsetDateTime,
    @SerialName("fingerprint") val fingerprint: String,
)
