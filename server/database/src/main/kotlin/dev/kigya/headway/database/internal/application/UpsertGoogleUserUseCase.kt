package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.model.ExposedUser
import dev.kigya.headway.database.api.port.UpsertGoogleUserUseCaseContract
import dev.kigya.headway.database.internal.data.UsersRepositoryContract

internal class UpsertGoogleUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) : UpsertGoogleUserUseCaseContract {
    override suspend fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): ExposedUser = usersRepository.upsertGoogleUser(
        googleId = googleId,
        email = email,
        name = name,
        avatarUrl = avatarUrl,
    )
}
