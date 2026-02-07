package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract

internal class CreateGoogleUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) {
    suspend operator fun invoke(
        googleId: String,
        email: String,
        name: String,
        avatarUrl: String?,
        role: DatabaseUserRole,
    ): DatabaseUser = usersRepository.insertGoogleUser(
        googleId = googleId,
        email = email,
        name = name,
        avatarUrl = avatarUrl,
        role = role,
    )
}
