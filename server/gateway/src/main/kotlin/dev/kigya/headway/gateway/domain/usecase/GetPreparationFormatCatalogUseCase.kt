package dev.kigya.headway.gateway.domain.usecase

import dev.kigya.headway.gateway.domain.preparation.requirePreparationFacilitator
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.graphql.GatewayAppLocale
import dev.kigya.headway.gateway.mapping.toCatalogLocale
import dev.kigya.headway.gateway.mapping.toDatabase
import dev.kigya.headway.gateway.mapping.toGateway
import dev.kigya.headway.gateway.model.GatewayPreparationCatalogItem
import dev.kigya.headway.gateway.model.GatewayUser

internal class GetPreparationFormatCatalogUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        user: GatewayUser,
        locale: GatewayAppLocale,
    ): List<GatewayPreparationCatalogItem> {
        val facilitator = requirePreparationFacilitator(user)
        val dto = preparationRepository.getFormatCatalog(
            facilitatorId = facilitator.id,
            facilitatorRole = facilitator.role.toDatabase(),
            locale = locale.toCatalogLocale(),
        )
        return dto.items.map { it.toGateway() }
    }
}
