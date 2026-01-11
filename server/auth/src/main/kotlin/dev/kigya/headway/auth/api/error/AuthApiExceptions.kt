package dev.kigya.headway.auth.api.error

class GoogleIdTokenValidationException : Exception("Invalid Google ID token")

class FailedToCreateUserException : Exception("Failed to create user")

class UserNotActiveException(message: String) : Exception(message)

class InvalidRefreshTokenException(message: String) : Exception(message)
