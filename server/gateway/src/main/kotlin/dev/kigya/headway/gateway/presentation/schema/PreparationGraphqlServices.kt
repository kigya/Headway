package dev.kigya.headway.gateway.presentation.schema

import dev.kigya.headway.gateway.domain.usecase.FinishPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationEmployeeReadinessUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationFormatCatalogUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionStateUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSessionSummaryUseCase
import dev.kigya.headway.gateway.domain.usecase.GetPreparationSetupEmployeesUseCase
import dev.kigya.headway.gateway.domain.usecase.SelectPreparationSessionQuestionUseCase
import dev.kigya.headway.gateway.domain.usecase.StartPreparationSessionUseCase
import dev.kigya.headway.gateway.domain.usecase.SubmitPreparationOutcomeUseCase

internal data class PreparationGraphqlServices(
    val getPreparationSetupEmployees: GetPreparationSetupEmployeesUseCase,
    val getPreparationEmployeeReadiness: GetPreparationEmployeeReadinessUseCase,
    val getPreparationFormatCatalog: GetPreparationFormatCatalogUseCase,
    val startPreparationSession: StartPreparationSessionUseCase,
    val getPreparationSessionState: GetPreparationSessionStateUseCase,
    val submitPreparationOutcome: SubmitPreparationOutcomeUseCase,
    val selectPreparationSessionQuestion: SelectPreparationSessionQuestionUseCase,
    val finishPreparationSession: FinishPreparationSessionUseCase,
    val getPreparationSessionSummary: GetPreparationSessionSummaryUseCase,
)
