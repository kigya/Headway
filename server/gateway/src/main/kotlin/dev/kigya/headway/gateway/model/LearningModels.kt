package dev.kigya.headway.gateway.model

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
internal enum class GatewayLearningSkillGroup {
    @SerialName("HARD")
    HARD,

    @SerialName("SOFT")
    SOFT,
}

@Serializable
internal enum class GatewayLearningProgressState {
    @SerialName("NOT_ANSWERED")
    NOT_ANSWERED,

    @SerialName("PARTIALLY_ANSWERED")
    PARTIALLY_ANSWERED,

    @SerialName("ANSWERED")
    ANSWERED,
}

@Serializable
internal data class GatewayLearningTag(
    @SerialName("id")
    val id: Long,
    @SerialName("label")
    val label: String,
)

@Serializable
internal data class GatewayLearningQuestion(
    @SerialName("id")
    val id: Long,
    @SerialName("skillGroup")
    val skillGroup: GatewayLearningSkillGroup,
    @SerialName("text")
    val text: String,
    @SerialName("tags")
    val tags: List<GatewayLearningTag>,
    @SerialName("progress")
    val progress: GatewayLearningProgressState?,
)

@Serializable
internal data class GatewayLearningQuestionsCatalog(
    @SerialName("hardSkills")
    val hardSkills: List<GatewayLearningQuestion>,
    @SerialName("softSkills")
    val softSkills: List<GatewayLearningQuestion>,
    @SerialName("resumeHard")
    val resumeHard: Long?,
    @SerialName("resumeSoft")
    val resumeSoft: Long?,
)

@Serializable
internal data class GatewayLearningQuestionsPage(
    @SerialName("items")
    val items: List<GatewayLearningQuestion>,
    @SerialName("resumeHard")
    val resumeHard: Long?,
    @SerialName("resumeSoft")
    val resumeSoft: Long?,
)

@Serializable
internal data class GatewayLearningQuestionsSearchResult(
    @SerialName("hardSkills")
    val hardSkills: List<GatewayLearningQuestion>,
    @SerialName("softSkills")
    val softSkills: List<GatewayLearningQuestion>,
)

@Serializable
internal data class GatewayLearningQuestionRemark(
    @SerialName("id")
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    @SerialName("authorUserId")
    @Serializable(UUIDSerializer::class)
    val authorUserId: UUID,
    @SerialName("body")
    val body: String,
    @SerialName("createdAtEpochMillis")
    val createdAtEpochMillis: Long,
)
