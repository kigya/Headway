package dev.kigya.headway.auth.api.port

import dev.kigya.headway.auth.api.model.LoginWithGoogleResponse

interface LoginWithGoogleUseCaseContract {
    suspend operator fun invoke(
        idToken: String,
        fingerprint: String,
    ): LoginWithGoogleResponse
}
