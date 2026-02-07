package dev.kigya.headway.database.api.url

import dev.kigya.headway.common.extension.KoinHttpClient
import dev.kigya.headway.common.model.ServiceUrlHolder
import dev.kigya.headway.common.model.serviceUrlHolder

data object DatabaseKoinHttpClient : KoinHttpClient

val databaseServiceUrlHolder: ServiceUrlHolder<DatabaseKoinHttpClient> =
    serviceUrlHolder<DatabaseKoinHttpClient>("/internal/v1/database")
