package dev.kigya.headway.database.api.model.`in`

import dev.kigya.headway.common.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DatabaseLearningRemarkCreateRequestDto(
    @SerialName("subject_user_id")
    @Serializable(UUIDSerializer::class)
    val subjectUserId: UUID,
    @SerialName("body")
    val body: String,
)
