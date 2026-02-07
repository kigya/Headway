package dev.kigya.headway.gateway.port

import dev.kigya.headway.gateway.model.AuthPayload

interface LoginWithGoogleUseCaseContract {
    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
    ): AuthPayload
}
