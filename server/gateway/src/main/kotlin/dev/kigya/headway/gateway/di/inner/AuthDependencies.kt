package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.auth.api.url.AuthKoinHttpClient
import dev.kigya.headway.gateway.data.repository.AuthRepository
import dev.kigya.headway.gateway.domain.repository.AuthRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.gateway.domain.usecase.RefreshAccessTokenUseCase
import dev.kigya.headway.gateway.domain.usecase.ResolveCallerUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.authDependencies() {
    single { AuthRepository(httpClient = get(named<AuthKoinHttpClient>())) } bind AuthRepositoryContract::class
    singleOf(::LoginWithGoogleUseCase)
    singleOf(::RefreshAccessTokenUseCase)
    singleOf(::ResolveCallerUseCase)
}
