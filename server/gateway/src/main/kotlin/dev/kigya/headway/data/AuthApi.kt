package dev.kigya.headway.data

import dev.kigya.headway.ConfigurationValues
import dev.kigya.headway.domain.model.AuthResponse
import dev.kigya.headway.domain.model.RefreshTokenResponse
import ext.successBodyOrThrow
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post

internal suspend fun HttpClient.loginWithGoogle(
    idToken: String,
    fingerprint: String,
): AuthResponse? {
    val response = this.post("${ConfigurationValues.AUTH_SERVICE_URL}/google") {
        parameter("id_token", idToken)
        parameter("fingerprint", fingerprint)
    }

    return response.successBodyOrThrow()
}

internal suspend fun HttpClient.refreshToken(
    refreshToken: String,
    fingerprint: String,
): RefreshTokenResponse? {
    val response = this.post("${ConfigurationValues.AUTH_SERVICE_URL}/refreshToken") {
        parameter("refresh_token", refreshToken)
        parameter("fingerprint", fingerprint)
    }

    return response.successBodyOrThrow()
}
