package dev.kigya.headway.database.internal.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class ExposedAccountStatus(val slug: String) {
    @SerialName("INVITED")
    INVITED("INVITED"),

    @SerialName("ACTIVE")
    ACTIVE("ACTIVE"),

    @SerialName("REVOKED")
    REVOKED("REVOKED"),
}
