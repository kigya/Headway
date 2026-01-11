package dev.kigya.headway.database.api.routing.session.request

import dev.kigya.headway.database.api.serialization.OffsetDateTimeSerializer
import dev.kigya.headway.database.api.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.UUID

@Serializable
internal data class CreateSessionRequestDto(
    @SerialName("user_id")
    @Serializable(UUIDSerializer::class) val userId: UUID,
    @SerialName("refresh_token") val refreshToken: String,
    @SerialName("expires_in")
    @Serializable(OffsetDateTimeSerializer::class) val expiresIn: OffsetDateTime,
    @SerialName("fingerprint") val fingerprint: String,
)
