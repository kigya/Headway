package dev.kigya.headway.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class HomeScreenPayload(
    @SerialName("dateLabel") val dateLabel: String,
    @SerialName("greeting") val greeting: String,
    @SerialName("displayName") val displayName: String,
    @SerialName("roleLabel") val roleLabel: String?,
    @SerialName("avatarUrl") val avatarUrl: String?,
    @SerialName("accessRole") val accessRole: GatewayUserRole,
    @SerialName("readinessPercent") val readinessPercent: Int?,
    @SerialName("nextInterviewType") val nextInterviewType: HomeScreenNextInterviewType?,
    @SerialName("nextInterviewTypeLabel") val nextInterviewTypeLabel: String?,
    @SerialName("sections") val sections: List<HomeScreenSectionItem>,
)

@Serializable
internal data class HomeScreenSectionItem(
    @SerialName("id") val id: HomeScreenSectionId,
    @SerialName("title") val title: String,
    @SerialName("style") val style: HomeScreenSectionStyle,
    @SerialName("iconUrl") val iconUrl: String,
)

@Serializable
internal enum class HomeScreenSectionId {
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

@Serializable
internal enum class HomeScreenSectionStyle {
    @SerialName("FILLED_PRIMARY")
    FILLED_PRIMARY,

    @SerialName("OUTLINED_ACCENT")
    OUTLINED_ACCENT,

    @SerialName("DEFAULT")
    DEFAULT,
}

@Serializable
internal enum class HomeScreenNextInterviewType {
    @SerialName("CHECK")
    CHECK,

    @SerialName("SPOT")
    SPOT,

    @SerialName("MOCK")
    MOCK,

    @SerialName("SOFT_SKILLS")
    SOFT_SKILLS,
}
