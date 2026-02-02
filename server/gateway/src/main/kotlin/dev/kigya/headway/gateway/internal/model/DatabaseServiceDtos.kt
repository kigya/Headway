package dev.kigya.headway.gateway.internal.model

import dev.kigya.headway.common.extension.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class InviteUserRequest(
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
)

@Serializable
internal data class DatabaseServiceUserDto(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)

@Serializable
internal data class DatabaseServiceErrorResponse(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
)
