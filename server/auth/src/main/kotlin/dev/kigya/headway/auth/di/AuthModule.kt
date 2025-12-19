package dev.kigya.headway.auth.di

import dev.kigya.headway.auth.data.google.GoogleAuthServiceImpl
import dev.kigya.headway.auth.data.jwt.JWTServiceImpl
import dev.kigya.headway.auth.domain.service.GoogleAuthService
import dev.kigya.headway.auth.domain.service.JWTService
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val authModule = module {

    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = false
                        ignoreUnknownKeys = true
                    }
                )
            }
        }
    }

    singleOf<JWTService>(::JWTServiceImpl)

    single<GoogleAuthService> {
        GoogleAuthServiceImpl(get(), get())
    }
}
