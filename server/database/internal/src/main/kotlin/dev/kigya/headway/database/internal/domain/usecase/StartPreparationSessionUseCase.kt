package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.domain.PreparationFacilitatorPolicy
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import java.util.UUID

internal class StartPreparationSessionUseCase(
    private val preparationRepository: PreparationRepositoryContract,
) {
    suspend operator fun invoke(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
        formatCode: DatabasePreparationFormatCode,
        preparationLanguage: String?,
        customDomainFilter: DatabasePreparationQuestionDomain?,
    ): DatabasePreparationStartSessionResponseDto {
        PreparationFacilitatorPolicy.ensurePreparationRole(facilitatorRole)
        val resolvedLanguage = resolvePreparationLanguage(
            formatCode = formatCode,
            preparationLanguage = preparationLanguage,
        )
        return preparationRepository.startSession(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
            formatCode = formatCode,
            preparationLanguage = resolvedLanguage,
            customDomainFilter = customDomainFilter,
        )
    }
}

private fun resolvePreparationLanguage(
    formatCode: DatabasePreparationFormatCode,
    preparationLanguage: String?,
): String {
    if (formatCode == DatabasePreparationFormatCode.CUSTOM) {
        val trimmed = preparationLanguage?.trim().orEmpty()
        if (trimmed.isEmpty()) {
            throw DatabaseException.InvalidRequest("Custom format requires preparation language")
        }
        return trimmed.lowercase()
    }
    if (preparationLanguage.isNullOrBlank()) {
        return when (formatCode) {
            DatabasePreparationFormatCode.CHECK -> "ru"
            else -> "en"
        }
    }
    return preparationLanguage.trim().lowercase()
}
