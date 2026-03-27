package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationSessionState
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class FinishPreparationSessionUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        sessionId: UUID,
    ): GatewayPreparationSessionState {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.finishSession(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            sessionId = sessionId,
        )
        return dto.toGateway()
    }
}
