package dev.kigya.headway.auth.internal.application

import dev.kigya.headway.auth.api.error.DependencyUnavailableException
import dev.kigya.headway.auth.api.error.FailedToCreateUserException
import dev.kigya.headway.auth.api.error.UserNotActiveException
import dev.kigya.headway.auth.api.model.LoginWithGoogleResponse
import dev.kigya.headway.auth.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.auth.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.auth.internal.google.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.security.JWTServiceContract
import org.slf4j.LoggerFactory
import java.time.ZoneOffset
import java.util.Calendar

internal class LoginWithGoogleUseCase(
    private val googleTokenVerifier: GoogleTokenVerifierContract,
    private val databaseServiceClient: DatabaseServiceClientContract,
    private val jwtService: JWTServiceContract,
) : LoginWithGoogleUseCaseContract {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun invoke(
        idToken: String,
        fingerprint: String,
    ): LoginWithGoogleResponse {
        val googleUser = googleTokenVerifier.verify(tokenString = idToken)

        val user = databaseServiceClient.upsertGoogleUser(
            googleId = googleUser.googleId,
            email = googleUser.email,
            name = googleUser.name,
            avatarUrl = googleUser.pictureUrl,
        ) ?: throw FailedToCreateUserException()

        if (user.isActive.not()) {
            throw UserNotActiveException("User is not active")
        }

        val expirationDate = Calendar.getInstance().apply { add(Calendar.MONTH, 3) }
        val accessToken = jwtService.generateAccessToken(user.id)
        val refreshToken = jwtService.generateRefreshToken(user.id, expirationDate.time)

        val sessionCreated = databaseServiceClient.createSession(
            userId = user.id,
            refreshToken = refreshToken,
            expiresIn = expirationDate.toInstant().atOffset(ZoneOffset.UTC),
            fingerprint = fingerprint,
        )
        if (!sessionCreated) {
            logger.warn("Failed to create refresh session for userId=${user.id} fingerprint=$fingerprint")
            throw DependencyUnavailableException()
        }

        return LoginWithGoogleResponse(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
