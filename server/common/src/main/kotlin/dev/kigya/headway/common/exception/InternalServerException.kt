package dev.kigya.headway.common.exception

import kotlinx.serialization.Serializable
import dev.kigya.headway.common.model.CommonApiError

@Serializable
data class InternalServerException(
    val error: CommonApiError,
) : Exception()
