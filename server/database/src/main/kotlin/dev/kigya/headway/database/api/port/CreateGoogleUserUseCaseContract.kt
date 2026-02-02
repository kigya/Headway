package dev.kigya.headway.database.api.port

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.model.ExposedUserRole

interface CreateGoogleUserUseCaseContract {
    suspend operator fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: ExposedUserRole,
    ): ExposedUser
}
