package dev.kigya.headway.common.extension

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.qualifier.named

interface KoinHttpClient

inline fun <reified ServiceKoinName> Module.createServiceHttpClient(
    host: String,
    port: Int,
    baseUrl: String,
) {
    single(named<ServiceKoinName>()) {
        HttpClient(CIO) {
            baseConfig()
            val basePath = '/' + baseUrl.trim().trim('/')

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTP
                    this.host = host
                    this.port = port

                    val reqPath = encodedPath.ifBlank { "/" }
                    val joined = basePath.trimEnd('/') + "/" + reqPath.trimStart('/')
                    encodedPath = joined.replace(Regex("/{2,}"), "/")
                }
            }
        }
    }
}

fun HttpClientConfig<CIOEngineConfig>.baseConfig() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = false
                ignoreUnknownKeys = true
            },
        )
    }
    install(Resources)
    install(HttpTimeout)
}
