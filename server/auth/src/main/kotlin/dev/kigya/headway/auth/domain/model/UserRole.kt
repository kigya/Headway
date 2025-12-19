package dev.kigya.headway.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal enum class UserRole(val slug: String) {
    DEVELOPER("developer"),
    MANAGER("manager"),
    MENTOR("mentor"),
    EMPLOYEE("employee"),
    GUEST("guest"), ;
}
