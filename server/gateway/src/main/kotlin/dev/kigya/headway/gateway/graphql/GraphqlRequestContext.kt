package dev.kigya.headway.gateway.graphql

internal data class GraphqlRequestContext(
    val authorizationHeader: String?,
    val appLocale: GatewayAppLocale,
)
