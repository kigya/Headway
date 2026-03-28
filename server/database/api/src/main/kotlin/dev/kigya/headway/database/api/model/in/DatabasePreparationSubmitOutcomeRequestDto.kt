package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationSubmitOutcomeRequestDto(
    @SerialName("sessionQuestionId")
    @Serializable(UUIDSerializer::class)
    val sessionQuestionId: UUID,
    @SerialName("outcome")
    val outcome: DatabasePreparationOutcomeCode,
    @SerialName("comment")
    val comment: String? = null,
)
