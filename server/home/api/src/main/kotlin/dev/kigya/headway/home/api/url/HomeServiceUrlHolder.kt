package dev.kigya.headway.home.api.url

import dev.kigya.headway.common.extension.KoinHttpClient
import dev.kigya.headway.common.model.ServiceUrlHolder
import dev.kigya.headway.common.model.serviceUrlHolder

data object HomeKoinHttpClient : KoinHttpClient

val homeServiceUrlHolder: ServiceUrlHolder<HomeKoinHttpClient> =
    serviceUrlHolder<HomeKoinHttpClient>("/internal/v1/home")
