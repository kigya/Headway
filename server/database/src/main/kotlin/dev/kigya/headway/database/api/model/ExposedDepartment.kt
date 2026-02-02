package dev.kigya.headway.database.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ExposedDepartment(val slug: String) {
    @SerialName("ANDROID")
    ANDROID("ANDROID"),

    @SerialName("IOS")
    IOS("IOS"),

    @SerialName("CROSS_PLATFORM")
    CROSSPLATFORM("CROSS_PLATFORM"),
}
