package dev.kigya.headway.database.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationStartSessionResponseDto(
    @SerialName("sessionId")
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    @SerialName("state")
    val state: DatabasePreparationSessionStateDto,
)

@Serializable
data class DatabasePreparationSessionStateDto(
    @SerialName("sessionId")
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    @SerialName("status")
    val status: DatabasePreparationSessionStatus,
    @SerialName("formatCode")
    val formatCode: DatabasePreparationFormatCode,
    @SerialName("preparationLanguage")
    val preparationLanguage: String,
    @SerialName("facilitatorUserId")
    @Serializable(UUIDSerializer::class)
    val facilitatorUserId: UUID,
    @SerialName("subjectUserId")
    @Serializable(UUIDSerializer::class)
    val subjectUserId: UUID,
    @SerialName("subjectDisplayName")
    val subjectDisplayName: String,
    @SerialName("subjectAvatarUrl")
    val subjectAvatarUrl: String? = null,
    @SerialName("currentQuestionId")
    @Serializable(UUIDSerializer::class)
    val currentQuestionId: UUID? = null,
    @SerialName("lastActiveQuestionId")
    @Serializable(UUIDSerializer::class)
    val lastActiveQuestionId: UUID? = null,
    @SerialName("customDomainFilter")
    val customDomainFilter: DatabasePreparationQuestionDomain? = null,
    @SerialName("questions")
    val questions: List<DatabasePreparationSessionQuestionDto>,
    @SerialName("startedAtEpochMillis")
    val startedAtEpochMillis: Long,
    @SerialName("endedAtEpochMillis")
    val endedAtEpochMillis: Long? = null,
)

@Serializable
data class DatabasePreparationSessionQuestionDto(
    @SerialName("id")
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("sortOrder")
    val sortOrder: Int,
    @SerialName("topicTags")
    val topicTags: List<String>,
    @SerialName("domain")
    val domain: DatabasePreparationQuestionDomain? = null,
    @SerialName("titleOrPrompt")
    val titleOrPrompt: String,
    @SerialName("body")
    val body: String? = null,
    @SerialName("outcome")
    val outcome: DatabasePreparationOutcomeCode? = null,
    @SerialName("comment")
    val comment: String? = null,
    @SerialName("addressedForUi")
    val addressedForUi: Boolean,
)

@Serializable
data class DatabasePreparationSessionSummaryDto(
    @SerialName("sessionId")
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    @SerialName("durationSeconds")
    val durationSeconds: Long,
    @SerialName("headline")
    val headline: String,
    @SerialName("body")
    val body: String,
)
