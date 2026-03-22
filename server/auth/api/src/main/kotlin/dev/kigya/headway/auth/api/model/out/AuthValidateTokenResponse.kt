package dev.kigya.headway.auth.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AuthValidateTokenResponse(
    @SerialName("user_uuid")
    @Serializable(UUIDSerializer::class)
    val userUuid: UUID,
    @SerialName("is_valid")
    val isValid: Boolean,
)
