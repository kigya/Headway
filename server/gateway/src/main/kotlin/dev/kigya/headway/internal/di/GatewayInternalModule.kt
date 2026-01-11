package dev.kigya.headway.internal.di

import dev.kigya.headway.api.port.CheckHealthStatusUseCaseContract
import dev.kigya.headway.api.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.api.port.RefreshTokenUseCaseContract
import dev.kigya.headway.internal.application.CheckHealthStatusUseCase
import dev.kigya.headway.internal.application.LoginWithGoogleUseCase
import dev.kigya.headway.internal.application.RefreshTokenUseCase
import dev.kigya.headway.internal.client.AuthServiceClientContract
import dev.kigya.headway.internal.client.HttpAuthServiceClient
import dev.kigya.headway.internal.probe.auth.AuthProbeContract
import dev.kigya.headway.internal.probe.auth.HttpAuthProbe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val gatewayInternalModule = module {
    singleHttpClient()
    healthDependencies()
    authDependencies()
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
            install(HttpTimeout)
        }
    }
}

private fun Module.healthDependencies() {
    singleOf(::HttpAuthProbe) bind AuthProbeContract::class
    singleOf(::CheckHealthStatusUseCase) bind CheckHealthStatusUseCaseContract::class
}

private fun Module.authDependencies() {
    singleOf(::HttpAuthServiceClient) bind AuthServiceClientContract::class
    singleOf(::LoginWithGoogleUseCase) bind LoginWithGoogleUseCaseContract::class
    singleOf(::RefreshTokenUseCase) bind RefreshTokenUseCaseContract::class
}
