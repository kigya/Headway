package dev.kigya.headway.database.api.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExposedUserRole(val slug: String) {
    DEVELOPER("DEVELOPER"),
    MANAGER("MANAGER"),
    MENTOR("MENTOR"),
    EMPLOYEE("EMPLOYEE"),
    GUEST("GUEST"), ;
}
