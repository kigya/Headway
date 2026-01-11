package dev.kigya.headway.auth.internal.google

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import dev.kigya.headway.auth.api.error.GoogleIdTokenValidationException
import dev.kigya.headway.auth.internal.model.GoogleUserPayload
import org.slf4j.LoggerFactory
import java.util.Collections

private const val KEY_USER_PICTURE = "picture"
private const val KEY_USER_NAME = "name"

/**
 * Google ID token verification is internal infrastructure.
 * API layer only observes domain-ish exceptions and payload mapping.
 */
internal class GoogleIdTokenVerifier : GoogleTokenVerifierContract {

    private val logger = LoggerFactory.getLogger(javaClass)
    private val transport = NetHttpTransport()
    private val jsonFactory = GsonFactory.getDefaultInstance()

    private val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
        // TODO: move to env/config when you introduce config module for the service
        .setAudience(Collections.singletonList("407408718192.apps.googleusercontent.com"))
        .build()

    override suspend fun verify(tokenString: String): GoogleUserPayload {
        val idToken: GoogleIdToken? = verifier.verify(tokenString)
        if (idToken == null) {
            logger.warn("Invalid ID token received")
            throw GoogleIdTokenValidationException()
        }

        val payload = idToken.payload
        return GoogleUserPayload(
            googleId = payload.subject,
            email = payload.email,
            isEmailVerified = payload.emailVerified,
            name = (payload[KEY_USER_NAME] as? String).orEmpty(),
            pictureUrl = payload[KEY_USER_PICTURE] as? String,
        )
    }
}
