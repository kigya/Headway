package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.gateway.data.repository.HomeRepository
import dev.kigya.headway.gateway.domain.repository.HomeRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.home.api.url.HomeKoinHttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.homeScreenDependencies() {
    single {
        HomeRepository(httpClient = get(named<HomeKoinHttpClient>()))
    } bind HomeRepositoryContract::class
    single {
        GetHomeScreenUseCase(
            homeRepository = get(),
        )
    }
}
