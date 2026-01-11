package dev.kigya.headway.auth.internal.di

import dev.kigya.headway.auth.api.port.AuthUseCaseContract
import dev.kigya.headway.auth.internal.application.AuthUseCase
import dev.kigya.headway.auth.internal.client.DatabaseServiceClientContract
import dev.kigya.headway.auth.internal.client.HttpDatabaseServiceClient
import dev.kigya.headway.auth.internal.google.GoogleIdTokenVerifier
import dev.kigya.headway.auth.internal.google.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.security.JWTServiceContract
import dev.kigya.headway.auth.internal.security.JWTServiceImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val authInternalModule = module {
    singleHttpClient()

    singleOf(::JWTServiceImpl) bind JWTServiceContract::class
    singleOf(::HttpDatabaseServiceClient) bind DatabaseServiceClientContract::class
    singleOf(::GoogleIdTokenVerifier) bind GoogleTokenVerifierContract::class

    singleOf(::AuthUseCase) bind AuthUseCaseContract::class
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
