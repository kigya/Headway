package dev.kigya.headway.auth.internal.google

import dev.kigya.headway.auth.internal.model.GoogleUserPayload

internal interface GoogleTokenVerifierContract {
    suspend fun verify(tokenString: String): GoogleUserPayload
}
