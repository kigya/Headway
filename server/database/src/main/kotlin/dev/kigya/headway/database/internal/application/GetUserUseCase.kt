package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.port.GetUserUseCaseContract
import dev.kigya.headway.database.internal.data.UsersRepositoryContract
import java.util.UUID

internal class GetUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) : GetUserUseCaseContract {
    override suspend fun invoke(googleId: String?, userId: UUID?): ExposedUser? {
        return when {
            userId != null -> usersRepository.readById(userId)
            googleId != null -> usersRepository.readByGoogleId(googleId)
            else -> null
        }
    }
}
