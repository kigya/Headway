package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class GetPreparationSessionSummaryUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        locale: String,
    ): DatabasePreparationSessionSummaryDto = preparationRepository.getSessionSummary(
        facilitatorId = facilitatorId,
        facilitatorRole = facilitatorRole,
        sessionId = sessionId,
        locale = locale,
    )
}
