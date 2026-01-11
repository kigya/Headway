package exception

import model.CommonApiError

class UserNotExistsException(
    val error: CommonApiError,
) : Exception()
