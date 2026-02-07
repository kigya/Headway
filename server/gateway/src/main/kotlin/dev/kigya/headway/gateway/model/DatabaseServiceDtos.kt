package dev.kigya.headway.gateway.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class DatabaseServiceErrorResponse(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
)
