package dev.kigya.headway.home.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class HomeAppLocaleDto {
    @SerialName("EN")
    EN,

    @SerialName("RU")
    RU,
}
