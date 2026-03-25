package dev.kigya.headway.home.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeScreenSectionItemDto(
    @SerialName("id") val id: HomeScreenSectionIdDto,
    @SerialName("title") val title: String,
    @SerialName("style") val style: HomeScreenSectionStyleDto,
    @SerialName("icon_url") val iconUrl: String,
)
