package dev.kigya.headway.schema

import com.apurebase.kgraphql.schema.dsl.SchemaBuilder
import dev.kigya.headway.data.loginWithGoogle
import dev.kigya.headway.data.refreshToken
import dev.kigya.headway.domain.model.AuthResponse
import dev.kigya.headway.domain.model.RefreshTokenResponse
import dev.kigya.headway.domain.model.User
import dev.kigya.headway.domain.model.UserRole
import io.ktor.client.HttpClient

internal fun SchemaBuilder.authSchema(client: HttpClient) {
    enum<UserRole>()
    type<User>()
    type<AuthResponse>()
    type<RefreshTokenResponse>()

    query("") { resolver { it: String -> "" } }
    mutation("loginWithGoogle") {
        description = "Authorization via Google ID Token"

        resolver { idToken: String, fingerprint: String ->
            client.loginWithGoogle(idToken, fingerprint)
        }
    }

    mutation("refreshToken") {
        description = "Refresh access token"

        resolver { refreshToken: String, fingerprint: String ->
            client.refreshToken(refreshToken, fingerprint)
        }
    }
}
