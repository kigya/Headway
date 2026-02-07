package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.gateway.client.AuthServiceClientContract
import dev.kigya.headway.gateway.client.HttpAuthServiceClient
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshTokenUseCase
import dev.kigya.headway.gateway.port.LoginWithGoogleUseCaseContract
import dev.kigya.headway.gateway.port.RefreshTokenUseCaseContract
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal fun Module.authDependencies() {
    singleOf(::HttpAuthServiceClient) bind AuthServiceClientContract::class
    singleOf(::LoginWithGoogleUseCase) bind LoginWithGoogleUseCaseContract::class
    singleOf(::RefreshTokenUseCase) bind RefreshTokenUseCaseContract::class
}
