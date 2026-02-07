package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class DatabaseUserRole(val slug: String) {
    @SerialName("DEVELOPER")
    DEVELOPER("DEVELOPER"),

    @SerialName("MANAGER")
    MANAGER("MANAGER"),

    @SerialName("MENTOR")
    MENTOR("MENTOR"),

    @SerialName("EMPLOYEE")
    EMPLOYEE("EMPLOYEE"),

    @SerialName("GUEST")
    GUEST("GUEST");
}
