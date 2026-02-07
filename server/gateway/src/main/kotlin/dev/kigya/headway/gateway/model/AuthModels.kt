package dev.kigya.headway.gateway.model

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PublicUser(
    @Serializable(UUIDSerializer::class) val id: UUID,
    val email: String,
    val name: String,
    val role: UserRole,
    val avatarUrl: String? = null,
)

@Serializable
data class AuthPayload(
    val accessToken: String,
    val refreshToken: String,
    val user: PublicUser,
)

@Serializable
data class RefreshTokenPayload(
    val accessToken: String,
)

@Serializable
enum class UserRole {
    DEVELOPER,
    MANAGER,
    MENTOR,
    EMPLOYEE,
    GUEST;
}

@Serializable
data class GatewayUser(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("department") val department: String,
    @SerialName("isActive") val isActive: Boolean,
    @SerialName("createdAt") val createdAt: Long,
    @SerialName("updatedAt") val updatedAt: Long,
)
