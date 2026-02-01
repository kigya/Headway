package dev.kigya.headway.gateway.api.port

import dev.kigya.headway.gateway.api.model.AuthPayload

interface LoginWithGoogleUseCaseContract {
    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
    ): AuthPayload
}
