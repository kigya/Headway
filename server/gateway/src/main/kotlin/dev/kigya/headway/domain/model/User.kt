package dev.kigya.headway.domain.model

import ext.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a user data model within the gateway service.
 * This class is used for serialization when communicating with clients.
 *
 * @property id The unique identifier of the user.
 * @property email The user's email address.
 * @property googleId The user's unique identifier from Google.
 * @property name The display name of the user.
 * @property role The role assigned to the user, determining their permissions.
 * @property avatarUrl An optional URL pointing to the user's avatar image.
 * @property isActive A flag indicating whether the user's account is active.
 * @property createdAt The timestamp (in milliseconds) when the user was created.
 * @property updatedAt The timestamp (in milliseconds) when the user was last updated.
 */
@Serializable
internal data class User(
    @SerialName("id")
    @Serializable(UUIDSerializer::class) val id: UUID,
    @SerialName("email") val email: String,
    @SerialName("google_id") val googleId: String,
    @SerialName("name") val name: String,
    @SerialName("role") val role: UserRole,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("is_active") val isActive: Boolean,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("updated_at") val updatedAt: Long,
)
