package dev.kigya.headway.database.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabasePreparationStartSessionResponseDto(
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    val state: DatabasePreparationSessionStateDto,
)

@Serializable
data class DatabasePreparationSessionStateDto(
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    val status: DatabasePreparationSessionStatus,
    val formatCode: DatabasePreparationFormatCode,
    val preparationLanguage: String,
    @Serializable(UUIDSerializer::class)
    val facilitatorUserId: UUID,
    @Serializable(UUIDSerializer::class)
    val subjectUserId: UUID,
    val subjectDisplayName: String,
    val subjectAvatarUrl: String? = null,
    @Serializable(UUIDSerializer::class)
    val currentQuestionId: UUID? = null,
    @Serializable(UUIDSerializer::class)
    val lastActiveQuestionId: UUID? = null,
    val customDomainFilter: DatabasePreparationQuestionDomain? = null,
    val questions: List<DatabasePreparationSessionQuestionDto>,
    val startedAtEpochMillis: Long,
    val endedAtEpochMillis: Long? = null,
)

@Serializable
data class DatabasePreparationSessionQuestionDto(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val sortOrder: Int,
    val topicTags: List<String>,
    val domain: DatabasePreparationQuestionDomain? = null,
    val titleOrPrompt: String,
    val body: String? = null,
    val outcome: DatabasePreparationOutcomeCode? = null,
    val comment: String? = null,
    val addressedForUi: Boolean,
)

@Serializable
data class DatabasePreparationSessionSummaryDto(
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    val durationSeconds: Long,
    val headline: String,
    val body: String,
)
