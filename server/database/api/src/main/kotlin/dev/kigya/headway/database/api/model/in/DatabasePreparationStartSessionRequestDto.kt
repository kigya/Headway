package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationStartSessionRequestDto(
    @SerialName("subjectUserId")
    @Serializable(UUIDSerializer::class)
    val subjectUserId: UUID,
    @SerialName("formatCode")
    val formatCode: DatabasePreparationFormatCode,
    @SerialName("preparationLanguage")
    val preparationLanguage: String? = null,
    @SerialName("customDomainFilter")
    val customDomainFilter: DatabasePreparationQuestionDomain? = null,
)
