package model

import kotlinx.serialization.Serializable

@Serializable
data class CommonApiError(
    val message: String,
    val stackTrace: List<String>? = null,
)
