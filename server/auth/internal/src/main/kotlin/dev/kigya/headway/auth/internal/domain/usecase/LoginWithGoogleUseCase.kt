package dev.kigya.headway.auth.internal.domain.usecase

import dev.kigya.headway.auth.api.model.out.AuthGoogleLoginResponse
import dev.kigya.headway.auth.internal.data.verifier.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.domain.error.AuthException
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import dev.kigya.headway.database.api.model.`in`.DatabaseSessionPlatform
import java.time.Clock
import java.time.ZoneOffset
import java.util.Date

internal class LoginWithGoogleUseCase(
    private val databaseRepository: DatabaseRepositoryContract,
    private val jwtRepository: JWTRepositoryContract,
    private val googleTokenVerifier: GoogleTokenVerifierContract,
    private val clock: Clock,
) {

    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
        platform: DatabaseSessionPlatform,
    ): AuthGoogleLoginResponse {
        val googleUser = googleTokenVerifier.verify(tokenString = idToken)

        val displayName = resolveGoogleDisplayName(
            name = googleUser.name,
            email = googleUser.email,
        )

        val user = databaseRepository.upsertGoogleUser(
            googleId = googleUser.googleId,
            email = googleUser.email,
            name = displayName,
            avatarUrl = googleUser.pictureUrl,
        )

        if (!user.isActive) {
            throw AuthException.UserNotActive()
        }

        val expirationInstant = clock.instant().atZone(ZoneOffset.UTC).plusMonths(REFRESH_ALIVE_MONTHS).toInstant()
        val expirationDate = Date.from(expirationInstant)
        val accessToken = jwtRepository.generateAccessToken(user.id)
        val refreshToken = jwtRepository.generateRefreshToken(user.id, expirationDate)

        databaseRepository.createSession(
            userId = user.id,
            refreshToken = refreshToken,
            expiresIn = expirationInstant.atOffset(ZoneOffset.UTC),
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

private fun resolveGoogleDisplayName(
    name: String,
    email: String,
): String {
    val trimmedName = name.trim()
    if (trimmedName.isNotEmpty()) {
        return trimmedName
    }
    val localPart = email.substringBefore('@').trim()
    if (localPart.isNotEmpty()) {
        return localPart
    }
    return DEFAULT_GOOGLE_DISPLAY_NAME
}

private const val DEFAULT_GOOGLE_DISPLAY_NAME: String = "User"

private const val REFRESH_ALIVE_MONTHS = 3L
