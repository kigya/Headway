package dev.kigya.headway.auth.data.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import dev.kigya.headway.auth.data.database.createSession
import dev.kigya.headway.auth.data.database.createUser
import dev.kigya.headway.auth.data.database.getUserByGoogleId
import dev.kigya.headway.auth.domain.model.AuthResponse
import dev.kigya.headway.auth.domain.model.FailedToCreateUserException
import dev.kigya.headway.auth.domain.model.GoogleIdTokenValidationException
import dev.kigya.headway.auth.domain.model.GoogleUserPayload
import exception.UserNotExistsException
import dev.kigya.headway.auth.domain.model.UserRole
import dev.kigya.headway.auth.domain.service.GoogleAuthService
import dev.kigya.headway.auth.domain.service.JWTService
import io.ktor.client.HttpClient
import org.slf4j.LoggerFactory
import java.util.Calendar
import java.util.Collections

private const val KEY_USER_PICTURE = "picture"

private const val KEY_USER_NAME = "name"

internal class GoogleAuthServiceImpl(
    private val client: HttpClient,
    private val jwtService: JWTService,
) : GoogleAuthService {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        .setAudience(Collections.singletonList("407408718192.apps.googleusercontent.com"))
        .build()

    override suspend fun verifyGoogleToken(tokenString: String): GoogleUserPayload {
        val idToken: GoogleIdToken? = verifier.verify(tokenString)

        if (idToken != null) {
            val payload = idToken.payload

            return GoogleUserPayload(
                googleId = payload.subject,
                email = payload.email,
                isEmailVerified = payload.emailVerified,
                name = (payload[KEY_USER_NAME] as? String).orEmpty(),
                pictureUrl = payload[KEY_USER_PICTURE] as? String,
            )
        } else {
            logger.warn("Invalid ID token received")
            throw GoogleIdTokenValidationException()
        }
    }

    override suspend  fun loginWithGoogle(idToken: String, fingerprint: String): AuthResponse {
        val googleUser = verifyGoogleToken(idToken)

        val user = try {
            client.getUserByGoogleId(googleUser.googleId)
        } catch (_: UserNotExistsException) {
            logger.warn("User with Google ID ${googleUser.googleId} not found")
            null
        }
        client.createUser(
            email = googleUser.email,
            name = googleUser.name,
            googleId = googleUser.googleId,
            avatarUrl = googleUser.pictureUrl,
            role = UserRole.EMPLOYEE,
        )
        if (user == null) throw FailedToCreateUserException()

        val expirationDate = Calendar.getInstance()
        expirationDate.add(Calendar.MONTH, 3)
        val accessToken = jwtService.generateAccessToken(user.id)
        val refreshToken = jwtService.generateRefreshToken(user.id, expirationDate.time)

        client.createSession(
            userId = user.id,
            refreshToken = refreshToken,
            expiresIn = expirationDate.toInstant().atOffset(java.time.ZoneOffset.UTC),
            fingerprint = fingerprint,
        )

        return AuthResponse(
            user = user,
            accessToken = accessToken,
            refreshToken = refreshToken,
        )
    }
}
