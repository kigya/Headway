package dev.kigya.headway.auth.data.database.request

import dev.kigya.headway.auth.ext.OffsetDateTimeSerializer
import dev.kigya.headway.auth.ext.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class CreateSessionRequestDto(
    @SerialName("user_id")
    @Serializable(UUIDSerializer::class) val userId: UUID,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in")
    @Serializable(OffsetDateTimeSerializer::class) val expiresIn: OffsetDateTime,
    @SerialName("fingerprint") val fingerprint: String,
)
