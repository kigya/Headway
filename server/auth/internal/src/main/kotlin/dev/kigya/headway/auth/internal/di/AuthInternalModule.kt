package dev.kigya.headway.auth.internal.di

import dev.kigya.headway.auth.api.url.authServiceUrlHolder
import dev.kigya.headway.auth.internal.core.ConfigurationValues
import dev.kigya.headway.auth.internal.core.ConfigurationValues.DATABASE_SERVICE_HOST
import dev.kigya.headway.auth.internal.core.ConfigurationValues.DATABASE_SERVICE_PORT
import dev.kigya.headway.auth.internal.domain.repository.JWTRepositoryContract
import dev.kigya.headway.auth.internal.data.jwt.JWTRepository
import dev.kigya.headway.auth.internal.data.jwt.JwtConfig
import dev.kigya.headway.auth.internal.data.repository.DatabaseRepository
import dev.kigya.headway.auth.internal.data.verifier.GoogleTokenVerifierContract
import dev.kigya.headway.auth.internal.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.auth.internal.domain.usecase.LoginWithGoogleUseCase
import dev.kigya.headway.auth.internal.domain.usecase.RefreshTokenUseCase
import dev.kigya.headway.auth.internal.data.verifier.GoogleIdTokenVerifier
import dev.kigya.headway.common.extension.createServiceHttpClient
import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.database.api.url.databaseServiceUrlHolder
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module
import java.time.Clock

internal val authInternalModule = module {
    createServiceHttpClient<DatabaseKoinHttpClient>(
        host = DATABASE_SERVICE_HOST,
        port = DATABASE_SERVICE_PORT,
        baseUrl = databaseServiceUrlHolder.baseUrl,
    )

    single {
        JwtConfig(
            issuer = ConfigurationValues.JWT_ISSUER,
            audience = ConfigurationValues.JWT_AUDIENCE,
            accessSecret = ConfigurationValues.JWT_ACCESS_SECRET,
            refreshSecret = ConfigurationValues.JWT_REFRESH_SECRET,
            accessTtlSec = ConfigurationValues.JWT_ACCESS_TTL_SEC,
        )
    }

    single<Clock> { Clock.systemUTC() }
    singleOf(::JWTRepository) bind JWTRepositoryContract::class
    single {
        DatabaseRepository(httpClient = get(named<DatabaseKoinHttpClient>()))
    } bind DatabaseRepositoryContract::class
    singleOf(::GoogleIdTokenVerifier) bind GoogleTokenVerifierContract::class

    singleOf(::LoginWithGoogleUseCase)
    singleOf(::RefreshTokenUseCase)
}
