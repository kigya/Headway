package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.gateway.data.repository.DatabaseRepository
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.databaseDependencies() {
    single { DatabaseRepository(httpClient = get(named<DatabaseKoinHttpClient>())) } bind DatabaseRepositoryContract::class
    singleOf(::InviteUserUseCase)
}
