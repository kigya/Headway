package dev.kigya.headway.database.internal.application

import dev.kigya.headway.database.api.port.CreateSessionUseCaseContract
import dev.kigya.headway.database.internal.data.RefreshSessionsRepositoryContract
import java.time.OffsetDateTime
import java.util.UUID

internal class CreateSessionUseCase(
    private val refreshSessionsRepository: RefreshSessionsRepositoryContract,
) : CreateSessionUseCaseContract {
    override suspend fun invoke(
        userId: UUID,
        refreshToken: String,
        expiresIn: OffsetDateTime,
        fingerprint: String,
    ) {
        refreshSessionsRepository.createSession(
            userId = userId,
            refreshToken = refreshToken,
            expiresIn = expiresIn,
            fingerprint = fingerprint,
        )
    }
}
