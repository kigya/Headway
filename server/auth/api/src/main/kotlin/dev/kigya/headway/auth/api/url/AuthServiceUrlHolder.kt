package dev.kigya.headway.auth.api.url

import dev.kigya.headway.common.extension.KoinHttpClient
import dev.kigya.headway.common.model.ServiceUrlHolder
import dev.kigya.headway.common.model.serviceUrlHolder

data object AuthKoinHttpClient : KoinHttpClient

val authServiceUrlHolder: ServiceUrlHolder<AuthKoinHttpClient> =
    serviceUrlHolder<AuthKoinHttpClient>("/internal/v1/auth")
