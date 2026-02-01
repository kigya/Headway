package dev.kigya.headway.auth.api.error

internal class GoogleIdTokenValidationException : Exception("Invalid Google ID token")
internal class FailedToCreateUserException : Exception("Failed to create user")
internal class UserNotActiveException(message: String) : Exception(message)
internal class InvalidRefreshTokenException(message: String) : Exception(message)
internal class BadRequestApiException : RuntimeException()
internal class DependencyUnavailableException : RuntimeException()
