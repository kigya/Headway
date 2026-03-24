package dev.kigya.headway.auth.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AuthPrincipalType {
    @SerialName("user")
    USER,

    @SerialName("guest")
    GUEST,
}
