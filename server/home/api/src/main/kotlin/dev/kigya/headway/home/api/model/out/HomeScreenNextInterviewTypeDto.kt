package dev.kigya.headway.home.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeScreenNextInterviewTypeDto {
    @SerialName("CHECK")
    CHECK,

    @SerialName("SPOT")
    SPOT,

    @SerialName("MOCK")
    MOCK,

    @SerialName("SOFT_SKILLS")
    SOFT_SKILLS,
}
