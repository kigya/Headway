package dev.kigya.headway.gateway.model

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PublicUser(
    @SerialName("id") @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("name") val name: String,
    @SerialName("role") val role: UserRole,
    @SerialName("avatarUrl") val avatarUrl: String? = null,
)

@Serializable
data class GatewayGoogleLoginResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("user") val user: PublicUser,
)

@Serializable
data class GatewayRefreshAccessTokenResponse(
    @SerialName("accessToken") val accessToken: String,
)

@Serializable
enum class UserRole {
    @SerialName("DEVELOPER")
    DEVELOPER,
    @SerialName("MANAGER")
    MANAGER,
    @SerialName("MENTOR")
    MENTOR,
    @SerialName("EMPLOYEE")
    EMPLOYEE,
    @SerialName("GUEST")
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
