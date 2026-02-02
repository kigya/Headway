package dev.kigya.headway.database.api.model

import kotlinx.serialization.Serializable

@Serializable
internal enum class ExposedAccountStatus(val slug: String) {
    INVITED("INVITED"),
    ACTIVE("ACTIVE"),
    REVOKED("REVOKED"),
}
