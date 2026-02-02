package dev.kigya.headway.database.internal.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.kigya.headway.database.api.port.CreateGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.CreateSessionUseCaseContract
import dev.kigya.headway.database.api.port.GetUserUseCaseContract
import dev.kigya.headway.database.api.port.InviteUserUseCaseContract
import dev.kigya.headway.database.api.port.UpsertGoogleUserUseCaseContract
import dev.kigya.headway.database.api.port.ValidateSessionUseCaseContract
import dev.kigya.headway.database.internal.application.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.application.CreateSessionUseCase
import dev.kigya.headway.database.internal.application.GetUserUseCase
import dev.kigya.headway.database.internal.application.InviteUserUseCase
import dev.kigya.headway.database.internal.application.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.application.ValidateSessionUseCase
import dev.kigya.headway.database.internal.config.ConfigurationValues
import dev.kigya.headway.database.internal.config.DatabaseConfig
import dev.kigya.headway.database.internal.data.RefreshSessionsRepository
import dev.kigya.headway.database.internal.data.RefreshSessionsRepositoryContract
import dev.kigya.headway.database.internal.data.UsersRepository
import dev.kigya.headway.database.internal.data.UsersRepositoryContract
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal val databaseModule = module {

    single {
        DatabaseConfig(
            host = ConfigurationValues.DATABASE_HOST_URL,
            port = ConfigurationValues.DATABASE_PORT,
            name = ConfigurationValues.DATABASE_NAME,
            user = ConfigurationValues.DATABASE_USER,
            password = ConfigurationValues.DATABASE_PASSWORD,
            sslMode = ConfigurationValues.DATABASE_SSL_MODE,
            poolSize = ConfigurationValues.DATABASE_POOL_SIZE,
        )
    }
    single { Database.connect(hikari(get())) }

    singleOf(::UsersRepository) bind UsersRepositoryContract::class
    singleOf(::RefreshSessionsRepository) bind RefreshSessionsRepositoryContract::class

    singleOf(::GetUserUseCase) bind GetUserUseCaseContract::class
    singleOf(::CreateGoogleUserUseCase) bind CreateGoogleUserUseCaseContract::class
    singleOf(::UpsertGoogleUserUseCase) bind UpsertGoogleUserUseCaseContract::class
    singleOf(::InviteUserUseCase) bind InviteUserUseCaseContract::class

    singleOf(::CreateSessionUseCase) bind CreateSessionUseCaseContract::class
    singleOf(::ValidateSessionUseCase) bind ValidateSessionUseCaseContract::class
}

private fun hikari(cfg: DatabaseConfig): HikariDataSource {
    val config = HikariConfig()

    config.driverClassName = "org.postgresql.Driver"
    config.jdbcUrl = "jdbc:postgresql://${cfg.host}:${cfg.port}/${cfg.name}?sslmode=${cfg.sslMode}"
    config.username = cfg.user
    config.password = cfg.password

    config.maximumPoolSize = cfg.poolSize
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

    config.validate()
    return HikariDataSource(config)
}
