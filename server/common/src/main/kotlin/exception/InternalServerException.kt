package exception

import kotlinx.serialization.Serializable
import model.CommonApiError

@Serializable
data class InternalServerException(
    val error: CommonApiError,
) : Exception()
