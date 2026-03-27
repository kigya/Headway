package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationReadiness
import dev.kigya.headway.gateway.model.GatewayUser
import java.util.UUID

internal class GetPreparationEmployeeReadinessUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        subjectUserId: UUID,
    ): GatewayPreparationReadiness {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.getEmployeeReadiness(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            subjectUserId = subjectUserId,
        )
        return dto.toGateway()
    }
}
