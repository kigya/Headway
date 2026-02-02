package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.model.ExposedUserRole
import dev.kigya.headway.database.api.port.CreateGoogleUserUseCaseContract
import dev.kigya.headway.database.internal.data.UsersRepositoryContract

internal class CreateGoogleUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) : CreateGoogleUserUseCaseContract {
    override suspend fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: ExposedUserRole,
    ): ExposedUser = usersRepository.insertGoogleUser(
        googleId = googleId,
        email = email,
        name = name,
        avatarUrl = avatarUrl,
        role = role,
    )
}
