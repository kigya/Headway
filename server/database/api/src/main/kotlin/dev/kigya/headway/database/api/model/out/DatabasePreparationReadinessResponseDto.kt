package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.Serializable

@Serializable
data class DatabasePreparationReadinessResponseDto(
    val readinessPercent: Short? = null,
    val recommendedFormatCode: DatabasePreparationFormatCode,
    val recommendedLabel: String? = null,
)
