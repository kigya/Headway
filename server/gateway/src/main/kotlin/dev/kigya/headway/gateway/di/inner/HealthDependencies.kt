package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.auth.api.url.AuthKoinHttpClient
import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.gateway.data.probe.AuthServiceProbe
import dev.kigya.headway.gateway.data.probe.DatabaseServiceProbe
import dev.kigya.headway.gateway.domain.usecase.CheckHealthStatusUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.named

internal fun Module.healthDependencies() {
    single {
        CheckHealthStatusUseCase(
            authProbe = AuthServiceProbe(get(named<AuthKoinHttpClient>())),
            databaseProbe = DatabaseServiceProbe(get(named<DatabaseKoinHttpClient>())),
        )
    }
}
