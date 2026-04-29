package dev.kigya.headway.home.api.model.out

import dev.kigya.headway.home.api.model.`in`.HomeUserRoleDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenResponseDto(
    @SerialName("date_label") val dateLabel: String,
    @SerialName("greeting") val greeting: String,
    @SerialName("display_name") val displayName: String,
    @SerialName("role_label") val roleLabel: String?,
    @SerialName("avatar_url") val avatarUrl: String?,
    @SerialName("access_role") val accessRole: HomeUserRoleDto,
    @SerialName("readiness_percent") val readinessPercent: Int?,
    @SerialName("next_interview_type") val nextInterviewType: HomeScreenNextInterviewTypeDto?,
    @SerialName("next_interview_type_label") val nextInterviewTypeLabel: String?,
    @SerialName("sections") val sections: List<HomeScreenSectionItemDto>,
)
