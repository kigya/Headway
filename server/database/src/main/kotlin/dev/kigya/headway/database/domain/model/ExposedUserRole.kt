package dev.kigya.headway.database.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal enum class ExposedUserRole(val slug: String) {
    DEVELOPER("developer"),
    MANAGER("manager"),
    MENTOR("mentor"),
    EMPLOYEE("employee"),
    GUEST("guest"), ;
}
