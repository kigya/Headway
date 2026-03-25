package dev.kigya.headway.home.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenResponseDto(
    @SerialName("date_label") val dateLabel: String,
    @SerialName("greeting") val greeting: String,
    @SerialName("role_label") val roleLabel: String?,
    @SerialName("readiness_percent") val readinessPercent: Int?,
    @SerialName("next_interview_type") val nextInterviewType: HomeScreenNextInterviewTypeDto?,
    @SerialName("next_interview_type_label") val nextInterviewTypeLabel: String?,
    @SerialName("sections") val sections: List<HomeScreenSectionItemDto>,
)
