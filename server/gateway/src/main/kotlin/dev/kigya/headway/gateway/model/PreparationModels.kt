package dev.kigya.headway.gateway.model

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal data class GatewayPreparationEmployee(
    @SerialName("id")
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("department")
    val department: GatewayUserDepartment?,
    @SerialName("avatarUrl")
    val avatarUrl: String?,
)

@Serializable
internal enum class GatewayPreparationFormatCode {
    @SerialName("CHECK")
    CHECK,

    @SerialName("SPOT")
    SPOT,

    @SerialName("MOCK")
    MOCK,

    @SerialName("SOFT_SKILLS")
    SOFT_SKILLS,

    @SerialName("CUSTOM")
    CUSTOM,
}

@Serializable
internal enum class GatewayPreparationSessionStatus {
    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("COMPLETED")
    COMPLETED,
}

@Serializable
internal enum class GatewayPreparationOutcomeCode {
    @SerialName("GOOD")
    GOOD,

    @SerialName("NOT_ANSWERED")
    NOT_ANSWERED,

    @SerialName("PARTIAL")
    PARTIAL,
}

@Serializable
internal enum class GatewayPreparationQuestionDomain {
    @SerialName("HARD")
    HARD,

    @SerialName("SOFT")
    SOFT,

    @SerialName("MIXED")
    MIXED,
}

@Serializable
internal data class GatewayPreparationReadiness(
    @SerialName("readinessPercent")
    val readinessPercent: Int?,
    @SerialName("recommendedFormat")
    val recommendedFormat: GatewayPreparationFormatCode,
    @SerialName("recommendedLabel")
    val recommendedLabel: String?,
)

@Serializable
internal data class GatewayPreparationCatalogItem(
    @SerialName("code")
    val code: GatewayPreparationFormatCode,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("tags")
    val tags: List<String>,
    @SerialName("defaultLanguage")
    val defaultLanguage: String?,
    @SerialName("selectableLanguages")
    val selectableLanguages: List<String>,
)

@Serializable
internal data class GatewayPreparationSessionQuestion(
    @SerialName("id")
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("order")
    val order: Int,
    @SerialName("topicTags")
    val topicTags: List<String>,
    @SerialName("domain")
    val domain: GatewayPreparationQuestionDomain?,
    @SerialName("text")
    val text: String,
    @SerialName("body")
    val body: String?,
    @SerialName("outcome")
    val outcome: GatewayPreparationOutcomeCode?,
    @SerialName("comment")
    val comment: String?,
    @SerialName("addressedForUi")
    val addressedForUi: Boolean,
)

@Serializable
internal data class GatewayPreparationSessionState(
    @SerialName("sessionId")
    @Serializable(UUIDSerializer::class)
    val sessionId: UUID,
    @SerialName("status")
    val status: GatewayPreparationSessionStatus,
    @SerialName("format")
    val format: GatewayPreparationFormatCode,
    @SerialName("language")
    val language: String,
    @SerialName("subjectDisplayName")
    val subjectDisplayName: String,
    @SerialName("subjectAvatarUrl")
    val subjectAvatarUrl: String?,
    @SerialName("currentQuestionId")
    @Serializable(UUIDSerializer::class)
    val currentQuestionId: UUID?,
    @SerialName("lastActiveQuestionId")
    @Serializable(UUIDSerializer::class)
    val lastActiveQuestionId: UUID?,
    @SerialName("customDomainFilter")
    val customDomainFilter: GatewayPreparationQuestionDomain?,
    @SerialName("questions")
    val questions: List<GatewayPreparationSessionQuestion>,
    @SerialName("startedAtEpochMillis")
    val startedAtEpochMillis: Long,
    @SerialName("endedAtEpochMillis")
    val endedAtEpochMillis: Long?,
)

@Serializable
internal data class GatewayPreparationSessionSummary(
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
