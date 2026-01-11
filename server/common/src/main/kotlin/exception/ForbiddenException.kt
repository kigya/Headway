package exception

import kotlinx.serialization.Serializable
import model.CommonApiError

@Serializable
data class ForbiddenException(
    val error: CommonApiError,
) : Exception()
