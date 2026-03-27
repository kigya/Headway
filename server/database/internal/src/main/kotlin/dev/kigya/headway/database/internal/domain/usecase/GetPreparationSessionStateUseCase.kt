package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class GetPreparationSessionStateUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = preparationRepository.getSessionState(
        facilitatorId = facilitatorId,
        facilitatorRole = facilitatorRole,
        sessionId = sessionId,
    )
}
