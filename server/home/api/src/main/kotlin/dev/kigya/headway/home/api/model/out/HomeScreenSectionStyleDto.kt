package dev.kigya.headway.home.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeScreenSectionStyleDto {
    @SerialName("FILLED_PRIMARY")
    FILLED_PRIMARY,

    @SerialName("OUTLINED_ACCENT")
    OUTLINED_ACCENT,

    @SerialName("DEFAULT")
    DEFAULT,
}
