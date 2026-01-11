package dev.kigya.headway.auth.internal.application

import dev.kigya.headway.auth.api.error.FailedToCreateUserException
import dev.kigya.headway.auth.api.error.InvalidRefreshTokenException
import dev.kigya.headway.auth.api.error.UserNotActiveException
import dev.kigya.headway.auth.api.model.AuthResponse
import dev.kigya.headway.auth.api.model.RefreshTokenResponse
import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import dev.kigya.headway.auth.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.auth.internal.google.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.security.JWTServiceContract
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.util.Calendar

internal class AuthUseCase(
    private val googleTokenVerifier: GoogleTokenVerifierContract,
    private val database: DatabaseServiceClientContract,
    private val jwtServiceContract: JWTServiceContract,
) : AuthUseCaseContract {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun loginWithGoogle(idToken: String, fingerprint: String): AuthResponse {
        val googleUser = googleTokenVerifier.verify(tokenString = idToken)

        val user = database.upsertGoogleUser(
            googleId = googleUser.googleId,
            email = googleUser.email,
            name = googleUser.name,
            avatarUrl = googleUser.pictureUrl,
        ) ?: throw FailedToCreateUserException()

        if (user.isActive.not()) {
            throw UserNotActiveException("User is not active")
        }

        val expirationDate = Calendar.getInstance().apply { add(Calendar.MONTH, 3) }
        val accessToken = jwtServiceContract.generateAccessToken(user.id)
        val refreshToken = jwtServiceContract.generateRefreshToken(user.id, expirationDate.time)

        val sessionCreated = database.createSession(
            userId = user.id,
            refreshToken = refreshToken,
            expiresIn = expirationDate.toInstant().atOffset(ZoneOffset.UTC),
            fingerprint = fingerprint,
        )
        if (!sessionCreated) {
            logger.warn("Failed to create refresh session for userId=${user.id} fingerprint=$fingerprint")
            throw RuntimeException("Failed to create refresh session")
        }

        return AuthResponse(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }

    override suspend fun refreshToken(refreshToken: String, fingerprint: String): RefreshTokenResponse {
        if (jwtServiceContract.verifyRefreshToken(refreshToken).not()) {
            throw InvalidRefreshTokenException("Invalid refresh token.")
        }

        val isValidSession = database.validateSession(
            refreshToken = refreshToken,
            fingerprint = fingerprint,
        )
        if (!isValidSession) {
            throw InvalidRefreshTokenException("Failed to validate session.")
        }

        val userUUID = jwtServiceContract.getUserUUID(refreshToken)
            ?: throw InvalidRefreshTokenException("Invalid refresh token. User UUID is null")

        val accessToken = jwtServiceContract.generateAccessToken(userUUID)
        return RefreshTokenResponse(accessToken)
    }
}
