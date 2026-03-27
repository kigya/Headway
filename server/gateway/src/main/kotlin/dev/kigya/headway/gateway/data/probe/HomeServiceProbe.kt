package dev.kigya.headway.gateway.data.probe

import dev.kigya.headway.gateway.data.probe.base.BaseHttpProbe
import io.ktor.client.HttpClient

internal open class HomeServiceProbe(
    httpClient: HttpClient,
) : BaseHttpProbe(httpClient)
