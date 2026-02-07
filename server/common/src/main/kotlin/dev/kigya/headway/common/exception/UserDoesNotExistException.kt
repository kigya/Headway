package dev.kigya.headway.common.exception

class UserDoesNotExistException(
    override val message: String,
) : Exception(message)
