package dev.kigya.headway.api.port

import dev.kigya.headway.api.model.AuthPayload
import dev.kigya.headway.api.model.RefreshTokenPayload

interface LoginWithGoogleUseCaseContract {
    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
    ): AuthPayload
}
