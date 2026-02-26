package dev.kigya.headway.database.api.model.`in`

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DatabaseSessionPlatform {
    @SerialName("ANDROID")
    ANDROID,

    @SerialName("IOS")
    IOS,

    @SerialName("DESKTOP")
    DESKTOP,

    @SerialName("WEB")
    WEB;
}
