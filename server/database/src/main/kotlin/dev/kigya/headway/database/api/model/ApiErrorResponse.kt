package dev.kigya.headway.database.api.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiErrorResponse(
    @SerialName("code") val code: String,
    @SerialName("message") val message: String,
)
