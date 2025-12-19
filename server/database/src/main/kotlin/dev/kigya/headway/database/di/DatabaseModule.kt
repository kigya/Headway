package dev.kigya.headway.database.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.kigya.headway.database.data.RefreshSessionServiceImpl
import dev.kigya.headway.database.data.UsersServiceImpl
import dev.kigya.headway.database.domain.service.RefreshSessionsService
import dev.kigya.headway.database.domain.service.UsersService
import org.jetbrains.exposed.v1.jdbc.Database
import org.koin.dsl.module

internal val databaseModule = module {

    single {
        Database.connect(hikari())
    }

    single<UsersService> { UsersServiceImpl(get()) }

    single<RefreshSessionsService> { RefreshSessionServiceImpl(get()) }
}

private fun hikari(): HikariDataSource {
    val config = HikariConfig()

    val host = System.getenv("DATABASE_HOST_URL")
    val port = System.getenv("DATABASE_PORT")
    val dbName = System.getenv("DATABASE_NAME")
    val user = System.getenv("DATABASE_USER")
    val password = System.getenv("DATABASE_PASSWORD")

    config.driverClassName = "org.postgresql.Driver"
    config.jdbcUrl = "jdbc:postgresql://$host:$port/$dbName?sslmode=require"
    config.username = user
    config.password = password

    config.maximumPoolSize = 10
    config.isAutoCommit = false
    config.transactionIsolation = "TRANSACTION_REPEATABLE_READ"

    config.validate()
    return HikariDataSource(config)
}
