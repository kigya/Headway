package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationSelectQuestionRequestDto(
    @Serializable(UUIDSerializer::class)
    val sessionQuestionId: UUID,
)
