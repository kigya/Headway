package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabasePreparationReadinessResponseDto(
    @SerialName("readinessPercent")
    val readinessPercent: Short? = null,
    @SerialName("recommendedFormatCode")
    val recommendedFormatCode: DatabasePreparationFormatCode,
    @SerialName("recommendedLabel")
    val recommendedLabel: String? = null,
)
