package dev.kigya.headway.auth.internal.data.verifier

import dev.kigya.headway.auth.api.model.`in`.AuthGoogleUserPayloadDto

internal interface GoogleTokenVerifierContract {
    suspend fun verify(tokenString: String): AuthGoogleUserPayloadDto
}
