package dev.kigya.headway.auth.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AuthValidateTokenResponse(
    @SerialName("principal_type")
    val principalType: AuthPrincipalType,
    @SerialName("user_uuid")
    @Serializable(UUIDSerializer::class)
    val userUuid: UUID? = null,
    @SerialName("guest_session_id")
    @Serializable(UUIDSerializer::class)
    val guestSessionId: UUID? = null,
    @SerialName("scopes")
    val scopes: List<String> = emptyList(),
)
