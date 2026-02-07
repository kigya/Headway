package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.OffsetDateTimeSerializer
import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
data class DatabaseCreateSessionPayloadDto(
    @SerialName("user_id") @Serializable(UUIDSerializer::class) val userId: UUID,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in") @Serializable(OffsetDateTimeSerializer::class) val expiresIn: OffsetDateTime,
    @SerialName("fingerprint") val fingerprint: String,
)
