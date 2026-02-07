package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.gateway.client.DatabaseServiceClientContract
import dev.kigya.headway.gateway.client.HttpDatabaseServiceClient
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.port.InviteUserUseCaseContract
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.databaseDependencies() {
    single { HttpDatabaseServiceClient(httpClient = get(named<DatabaseKoinHttpClient>())) } bind DatabaseServiceClientContract::class
    singleOf(::InviteUserUseCase) bind InviteUserUseCaseContract::class
}
