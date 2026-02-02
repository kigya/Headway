package dev.kigya.headway.database.api.port

import dev.kigya.headway.database.api.model.ExposedUser
import java.util.UUID

interface GetUserUseCaseContract {
    suspend operator fun invoke(
        googleId: String?,
        userId: UUID?,
    ): ExposedUser?
}
