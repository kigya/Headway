package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationSubmitOutcomeRequestDto(
    @Serializable(UUIDSerializer::class)
    val sessionQuestionId: UUID,
    val outcome: DatabasePreparationOutcomeCode,
    val comment: String? = null,
)
