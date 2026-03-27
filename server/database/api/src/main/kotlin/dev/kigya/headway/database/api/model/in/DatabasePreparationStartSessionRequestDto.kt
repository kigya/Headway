package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationStartSessionRequestDto(
    @Serializable(UUIDSerializer::class)
    val subjectUserId: UUID,
    val formatCode: DatabasePreparationFormatCode,
    val preparationLanguage: String? = null,
    val customDomainFilter: DatabasePreparationQuestionDomain? = null,
)
