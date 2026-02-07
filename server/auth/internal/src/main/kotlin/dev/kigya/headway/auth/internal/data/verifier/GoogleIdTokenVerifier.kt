package dev.kigya.headway.auth.internal.data.verifier

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import dev.kigya.headway.auth.api.model.`in`.AuthGoogleUserPayloadDto
import dev.kigya.headway.auth.internal.domain.error.AuthException
import java.util.Collections

internal class GoogleIdTokenVerifier : GoogleTokenVerifierContract {

    val verifier: GoogleIdTokenVerifier = GoogleIdTokenVerifier.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
    )
        // TODO: move to env/config when you introduce config module for the service
        .setAudience(Collections.singletonList("407408718192.apps.googleusercontent.com"))
        .build()

    override suspend fun verify(tokenString: String): AuthGoogleUserPayloadDto = try {
        val idToken = verifier.verify(tokenString) ?:
            throw AuthException.Unauthorized("Invalid Google token")

        val payload = idToken.payload

        if (!payload.emailVerified) {
            throw AuthException.Forbidden("Email is not verified")
        }

        AuthGoogleUserPayloadDto(
            googleId = payload.subject,
            email = payload.email,
            name = (payload[KEY_USER_NAME] as? String).orEmpty(),
            pictureUrl = payload[KEY_USER_PICTURE] as? String,
        )
    } catch (e: AuthException.Unauthorized) {
        throw e
    } catch (t: Throwable) {
        throw AuthException.DependencyUnavailable("google", cause = t)
    }
}

private const val KEY_USER_PICTURE = "picture"
private const val KEY_USER_NAME = "name"
