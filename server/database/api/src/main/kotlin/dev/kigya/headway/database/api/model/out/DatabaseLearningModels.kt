package dev.kigya.headway.database.api.model.out

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class DatabaseLearningSkillGroup {
    @SerialName("HARD")
    HARD,

    @SerialName("SOFT")
    SOFT,
}

@Serializable
enum class DatabaseLearningProgressState {
    @SerialName("NOT_ANSWERED")
    NOT_ANSWERED,

    @SerialName("PARTIALLY_ANSWERED")
    PARTIALLY_ANSWERED,

    @SerialName("ANSWERED")
    ANSWERED,
}

@Serializable
data class DatabaseLearningTagDto(
    @SerialName("id")
    val id: Long,
    @SerialName("label")
    val label: String,
)

@Serializable
data class DatabaseLearningQuestionDto(
    @SerialName("id")
    val id: Long,
    @SerialName("skillGroup")
    val skillGroup: DatabaseLearningSkillGroup,
    @SerialName("text")
    val text: String,
    @SerialName("tags")
    val tags: List<DatabaseLearningTagDto>,
    @SerialName("progress")
    val progress: DatabaseLearningProgressState?,
)

@Serializable
data class DatabaseLearningCatalogResponseDto(
    @SerialName("hardSkills")
    val hardSkills: List<DatabaseLearningQuestionDto>,
    @SerialName("softSkills")
    val softSkills: List<DatabaseLearningQuestionDto>,
    @SerialName("resumeHard")
    val resumeHard: Long?,
    @SerialName("resumeSoft")
    val resumeSoft: Long?,
)

@Serializable
data class DatabaseLearningPageResponseDto(
    @SerialName("items")
    val items: List<DatabaseLearningQuestionDto>,
    @SerialName("resumeHard")
    val resumeHard: Long?,
    @SerialName("resumeSoft")
    val resumeSoft: Long?,
)

@Serializable
data class DatabaseLearningSearchResponseDto(
    @SerialName("hardSkills")
    val hardSkills: List<DatabaseLearningQuestionDto>,
    @SerialName("softSkills")
    val softSkills: List<DatabaseLearningQuestionDto>,
)

@Serializable
data class DatabaseLearningRemarkDto(
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
