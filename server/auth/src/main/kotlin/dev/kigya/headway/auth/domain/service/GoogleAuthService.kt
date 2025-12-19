package dev.kigya.headway.auth.domain.service

import dev.kigya.headway.auth.domain.model.AuthResponse
import dev.kigya.headway.auth.domain.model.GoogleUserPayload

internal interface GoogleAuthService {

    suspend fun verifyGoogleToken(tokenString: String): GoogleUserPayload

    suspend fun loginWithGoogle(idToken: String, fingerprint: String): AuthResponse
}
