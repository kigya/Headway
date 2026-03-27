package dev.kigya.headway.database.internal.di

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import dev.kigya.headway.database.internal.core.config.ConfigurationValues
import dev.kigya.headway.database.internal.core.config.DatabaseConfig
import dev.kigya.headway.database.internal.data.repository.InterviewQuestionBankRepository
import dev.kigya.headway.database.internal.data.repository.PreparationRepository
import dev.kigya.headway.database.internal.data.repository.RefreshSessionsRepository
import dev.kigya.headway.database.internal.data.repository.UsersRepository
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.database.internal.domain.repository.RefreshSessionsRepositoryContract
import dev.kigya.headway.database.internal.domain.repository.UsersRepositoryContract
import dev.kigya.headway.database.internal.domain.usecase.BuildSessionQuestionSnapshotUseCase
import dev.kigya.headway.database.internal.domain.usecase.CreateGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.CreateSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.FinishPreparationSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationEmployeeReadinessUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationSessionStateUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationSessionSummaryUseCase
import dev.kigya.headway.database.internal.domain.usecase.InviteUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.ListPreparationFormatCatalogUseCase
import dev.kigya.headway.database.internal.domain.usecase.ListPreparationSetupEmployeesUseCase
import dev.kigya.headway.database.internal.domain.usecase.SelectPreparationSessionQuestionUseCase
import dev.kigya.headway.database.internal.domain.usecase.StartPreparationSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.SubmitPreparationOutcomeUseCase
import dev.kigya.headway.database.internal.domain.usecase.UpsertGoogleUserUseCase
import dev.kigya.headway.database.internal.domain.usecase.ValidateSessionUseCase
import dev.kigya.headway.database.internal.presentation.PreparationUseCases
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
    singleOf(::InterviewQuestionBankRepository)
    singleOf(::BuildSessionQuestionSnapshotUseCase)
    singleOf(::PreparationRepository) bind PreparationRepositoryContract::class

    singleOf(::GetGoogleUserUseCase)
    singleOf(::CreateGoogleUserUseCase)
    singleOf(::UpsertGoogleUserUseCase)
    singleOf(::InviteUserUseCase)
    singleOf(::CreateSessionUseCase)
    singleOf(::ValidateSessionUseCase)
    singleOf(::ListPreparationSetupEmployeesUseCase)
    singleOf(::GetPreparationEmployeeReadinessUseCase)
    singleOf(::ListPreparationFormatCatalogUseCase)
    singleOf(::StartPreparationSessionUseCase)
    singleOf(::GetPreparationSessionStateUseCase)
    singleOf(::SubmitPreparationOutcomeUseCase)
    singleOf(::SelectPreparationSessionQuestionUseCase)
    singleOf(::FinishPreparationSessionUseCase)
    singleOf(::GetPreparationSessionSummaryUseCase)

    single {
        PreparationUseCases(
            listSetupEmployees = get(),
            getEmployeeReadiness = get(),
            listFormatCatalog = get(),
            startSession = get(),
            getSessionState = get(),
            submitOutcome = get(),
            selectQuestion = get(),
            finishSession = get(),
            getSessionSummary = get(),
        )
    }
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
