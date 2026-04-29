package dev.kigya.headway.common.extension

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.resources.Resources
import kotlinx.serialization.json.Json

fun Application.defaultContentNegotiation() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                encodeDefaults = true
                ignoreUnknownKeys = true
            },
        )
    }
}

fun Application.defaultResources() {
    install(Resources)
}
