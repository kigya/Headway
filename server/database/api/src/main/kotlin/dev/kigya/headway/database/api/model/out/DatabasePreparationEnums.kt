package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DatabasePreparationFormatCode {
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
enum class DatabasePreparationSessionStatus {
    @SerialName("ACTIVE")
    ACTIVE,

    @SerialName("COMPLETED")
    COMPLETED,
}

@Serializable
enum class DatabasePreparationOutcomeCode {
    @SerialName("GOOD")
    GOOD,

    @SerialName("NOT_ANSWERED")
    NOT_ANSWERED,

    @SerialName("PARTIAL")
    PARTIAL,
}

@Serializable
enum class DatabasePreparationQuestionDomain {
    @SerialName("HARD")
    HARD,

    @SerialName("SOFT")
    SOFT,

    @SerialName("MIXED")
    MIXED,
}
