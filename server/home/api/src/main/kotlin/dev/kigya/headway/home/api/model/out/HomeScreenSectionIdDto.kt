package dev.kigya.headway.home.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeScreenSectionIdDto {
    @SerialName("HOME")
    HOME,

    @SerialName("START_TRAINING_SESSION")
    START_TRAINING_SESSION,

    @SerialName("LEARN_QUESTIONS")
    LEARN_QUESTIONS,

    @SerialName("EMPLOYEE_PROGRESS_MANAGEMENT")
    EMPLOYEE_PROGRESS_MANAGEMENT,

    @SerialName("PEOPLE_MANAGEMENT")
    PEOPLE_MANAGEMENT,

    @SerialName("VIEW_STATISTICS")
    VIEW_STATISTICS,

    @SerialName("ABOUT")
    ABOUT,

    @SerialName("SIGN_OUT")
    SIGN_OUT,
}
