package dev.kigya.headway.gateway.di.inner

import dev.kigya.headway.database.api.url.DatabaseKoinHttpClient
import dev.kigya.headway.gateway.data.repository.DatabaseRepository
import dev.kigya.headway.gateway.data.repository.LearningQuestionsRepository
import dev.kigya.headway.gateway.data.repository.PreparationRepository
import dev.kigya.headway.gateway.domain.repository.DatabaseRepositoryContract
import dev.kigya.headway.gateway.domain.repository.LearningQuestionsRepositoryContract
import dev.kigya.headway.gateway.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.gateway.domain.usecase.FinishPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationEmployeeReadinessUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationFormatCatalogUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionStateUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionSummaryUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSetupEmployeesUseCase
import dev.kigya.headway.gateway.domain.usecase.InviteUserUseCase
import dev.kigya.headway.gateway.domain.usecase.LearningQuestionsGraphqlUseCases
import dev.kigya.headway.gateway.domain.usecase.SelectPreparationSessionQuestionUseCase
import dev.kigya.headway.gateway.domain.usecase.StartPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.SubmitPreparationOutcomeUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.databaseDependencies() {
    single {
        DatabaseRepository(httpClient = get(named<DatabaseKoinHttpClient>()))
    } bind DatabaseRepositoryContract::class

    single {
        PreparationRepository(httpClient = get(named<DatabaseKoinHttpClient>()))
    } bind PreparationRepositoryContract::class

    single {
        LearningQuestionsRepository(httpClient = get(named<DatabaseKoinHttpClient>()))
    } bind LearningQuestionsRepositoryContract::class

    singleOf(::LearningQuestionsGraphqlUseCases)

    singleOf(::InviteUserUseCase)
    singleOf(::GetPreparationSetupEmployeesUseCase)
    singleOf(::GetPreparationEmployeeReadinessUseCase)
    singleOf(::GetPreparationFormatCatalogUseCase)
    singleOf(::StartPreparationSessionUseCase)
    singleOf(::GetPreparationSessionStateUseCase)
    singleOf(::SubmitPreparationOutcomeUseCase)
    singleOf(::SelectPreparationSessionQuestionUseCase)
    singleOf(::FinishPreparationSessionUseCase)
    singleOf(::GetPreparationSessionSummaryUseCase)
}
