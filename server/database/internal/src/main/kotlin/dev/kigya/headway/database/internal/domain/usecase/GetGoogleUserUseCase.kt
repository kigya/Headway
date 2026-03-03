package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabaseUser
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract
import java.util.UUID

internal class GetGoogleUserUseCase(
    private val usersRepository: UsersRepositoryContract,
) {
    suspend operator fun invoke(
        googleId: String?,
        userId: UUID?,
    ): DatabaseUser? = when {
        userId != null -> usersRepository.readById(userId)
        googleId != null -> usersRepository.readByGoogleId(googleId)
        else -> null
    }
}
