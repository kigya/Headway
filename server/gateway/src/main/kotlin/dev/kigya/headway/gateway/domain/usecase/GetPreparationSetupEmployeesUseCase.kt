package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationEmployee
import dev.kigya.headway.gateway.model.GatewayUser

internal class GetPreparationSetupEmployeesUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(user: GatewayUser): List<GatewayPreparationEmployee> {
        val facilitator = requirePreparationFacilitator(user)
        val upstream = preparationRepository.getSetupEmployees(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
        )
        return upstream.employees.map { it.toGateway() }
    }
}
