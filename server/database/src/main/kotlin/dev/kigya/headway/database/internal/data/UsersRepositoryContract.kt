package dev.kigya.headway.database.internal.data

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.model.ExposedUserRole
import java.util.UUID

internal interface UsersRepositoryContract {
    suspend fun readById(id: UUID): ExposedUser?
    suspend fun readByGoogleId(googleId: String): ExposedUser?
    suspend fun readByEmail(email: String): ExposedUser?

    suspend fun inviteUser(
        email: String,
        department: String,
    ): ExposedUser

    suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): ExposedUser

    suspend fun insertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: ExposedUserRole,
    ): ExposedUser

    suspend fun updateGoogleUser(
        userId: UUID,
        name: String,
        avatarUrl: String?
    ): ExposedUser?
}
