package dev.kigya.headway.database.api.port

import dev.kigya.headway.database.api.model.ExposedUser

interface InviteUserUseCaseContract {
    suspend operator fun invoke(
        email: String,
        department: String,
    ): ExposedUser
}
