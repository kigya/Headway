package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.internal.data.verifier.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import java.time.ZoneOffset
import java.util.Calendar

private const val JWT_REFRESH_ALIVE_MOTHS = 3

internal class LoginWithGoogleUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
    private val jwtRepository: JWTRepositoryContract,
    private val googleTokenVerifier: GoogleTokenVerifierContract,
) {

    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
        platform: DatabaseSessionPlatform,
    ): AuthGoogleLoginResponse {
        val googleUser = googleTokenVerifier.verify(tokenString = idToken)

        val user = databaseRepository.upsertGoogleUser(
            googleId = googleUser.googleId,
            email = googleUser.email,
            name = googleUser.name,
            avatarUrl = googleUser.pictureUrl,
        )

        if (!user.isActive) {
            throw AuthException.UserNotActive()
        }

        val expirationDate = Calendar.getInstance().apply { add(Calendar.MONTH, JWT_REFRESH_ALIVE_MOTHS) }
        val accessToken = jwtRepository.generateAccessToken(user.id)
        val refreshToken = jwtRepository.generateRefreshToken(user.id, expirationDate.time)

        databaseRepository.createSession(
            userId = user.id,
            refreshToken = refreshToken,
            expiresIn = expirationDate.toInstant().atOffset(ZoneOffset.UTC),
            fingerprint = fingerprint,
            platform = platform,
        )

        return AuthGoogleLoginResponse(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
