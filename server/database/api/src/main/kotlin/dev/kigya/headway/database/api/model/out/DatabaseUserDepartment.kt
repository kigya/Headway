package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DatabaseUserDepartment(val slug: String) {
    @SerialName("ANDROID")
    ANDROID("ANDROID"),

    @SerialName("IOS")
    IOS("IOS"),

    @SerialName("CROSS_PLATFORM")
    CROSSPLATFORM("CROSS_PLATFORM");
}
