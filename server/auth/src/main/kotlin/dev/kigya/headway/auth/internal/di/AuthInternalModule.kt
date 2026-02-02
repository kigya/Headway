package dev.kigya.headway.auth.internal.di

import dev.kigya.headway.auth.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.auth.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.auth.internal.application.LoginWithGoogleUseCase
import dev.kigya.headway.auth.internal.application.RefreshTokenUseCase
import dev.kigya.headway.auth.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.auth.internal.client.HttpDatabaseServiceClient
import dev.kigya.headway.auth.internal.config.ConfigurationValues
import dev.kigya.headway.auth.internal.google.GoogleIdTokenVerifier
import dev.kigya.headway.auth.internal.google.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.security.JWTServiceContract
import dev.kigya.headway.auth.internal.security.JWTServiceImpl
import dev.kigya.headway.auth.internal.security.JwtConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import java.time.Clock

internal val authInternalModule = module {
    singleHttpClient()

    single {
        JwtConfig(
            issuer = ConfigurationValues.JWT_ISSUER,
            audience = ConfigurationValues.JWT_AUDIENCE,
            accessSecret = ConfigurationValues.JWT_ACCESS_SECRET,
            refreshSecret = ConfigurationValues.JWT_REFRESH_SECRET,
            accessTtlSec = ConfigurationValues.JWT_ACCESS_TTL_SEC,
        )
    }

    single<Clock> { Clock.systemUTC() }
    singleOf(::JWTServiceImpl) bind JWTServiceContract::class
    singleOf(::HttpDatabaseServiceClient) bind DatabaseServiceClientContract::class
    singleOf(::GoogleIdTokenVerifier) bind GoogleTokenVerifierContract::class

    singleOf(::LoginWithGoogleUseCase) bind LoginWithGoogleUseCaseContract::class
    singleOf(::RefreshTokenUseCase) bind RefreshTokenUseCaseContract::class
}

private fun Module.singleHttpClient() {
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = false
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }
}
