package dev.kigya.headway.database.api.port

interface ValidateSessionUseCaseContract {
    suspend operator fun invoke(
        refreshToken: String,
        fingerprint: String,
    )
}
