package dev.kigya.headway.common.exception

import dev.kigya.headway.common.model.CommonApiError

class UserNotExistsException(
    val error: CommonApiError,
) : Exception()
