package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract

internal class UpsertGoogleUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) {
    suspend operator fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
    ): DatabaseUser = usersRepository.upsertGoogleUser(
        googleId = googleId,
        email = email,
        name = name,
        avatarUrl = avatarUrl,
    )
}
