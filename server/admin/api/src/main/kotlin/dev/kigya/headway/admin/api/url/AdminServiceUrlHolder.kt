package dev.kigya.headway.admin.api.url

import dev.kigya.headway.common.extension.KoinHttpClient
import dev.kigya.headway.common.model.ServiceUrlHolder
import dev.kigya.headway.common.model.serviceUrlHolder

data object AdminKoinHttpClient : KoinHttpClient

val adminServiceUrlHolder: ServiceUrlHolder<AdminKoinHttpClient> =
    serviceUrlHolder<AdminKoinHttpClient>("/internal/v1/admin")
