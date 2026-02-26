package dev.kigya.headway.database.internal.domain.repository

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import java.util.UUID

internal interface UsersRepositoryContract {
    suspend fun readById(id: UUID): DatabaseUser?
    suspend fun readByGoogleId(googleId: String): DatabaseUser?
    suspend fun readByEmail(email: String): DatabaseUser?

    suspend fun inviteUser(
        email: String,
        department: String,
    ): DatabaseUser

    suspend fun upsertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser

    suspend fun insertGoogleUser(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: DatabaseUserRole,
    ): DatabaseUser

    suspend fun updateGoogleUser(
        userId: UUID,
        name: String,
        avatarUrl: String?
    ): DatabaseUser?
}
