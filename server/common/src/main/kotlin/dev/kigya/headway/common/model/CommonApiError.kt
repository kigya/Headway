package dev.kigya.headway.common.model

import kotlinx.serialization.Serializable

@Serializable
data class CommonApiError(
    val message: String,
    val stackTrace: List<String>? = null,
)
