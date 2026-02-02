package dev.kigya.headway.database.api.port

import dev.kigya.headway.database.api.model.ExposedUser

interface UpsertGoogleUserUseCaseContract {
    suspend operator fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): ExposedUser
}
