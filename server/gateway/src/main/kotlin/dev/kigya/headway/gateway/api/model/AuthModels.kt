package dev.kigya.headway.gateway.api.model

import dev.kigya.headway.common.extension.UUIDSerializer
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
