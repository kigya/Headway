@file:Suppress("LongParameterList")

package dev.kigya.headway.database.internal.presentation

import dev.kigya.headway.database.internal.domain.usecase.FinishPreparationSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationEmployeeReadinessUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationSessionStateUseCase
import dev.kigya.headway.database.internal.domain.usecase.GetPreparationSessionSummaryUseCase
import dev.kigya.headway.database.internal.domain.usecase.ListPreparationFormatCatalogUseCase
import dev.kigya.headway.database.internal.domain.usecase.ListPreparationSetupEmployeesUseCase
import dev.kigya.headway.database.internal.domain.usecase.SelectPreparationSessionQuestionUseCase
import dev.kigya.headway.database.internal.domain.usecase.StartPreparationSessionUseCase
import dev.kigya.headway.database.internal.domain.usecase.SubmitPreparationOutcomeUseCase

internal data class PreparationUseCases(
    val listSetupEmployees: ListPreparationSetupEmployeesUseCase,
    val getEmployeeReadiness: GetPreparationEmployeeReadinessUseCase,
    val listFormatCatalog: ListPreparationFormatCatalogUseCase,
    val startSession: StartPreparationSessionUseCase,
    val getSessionState: GetPreparationSessionStateUseCase,
    val submitOutcome: SubmitPreparationOutcomeUseCase,
    val selectQuestion: SelectPreparationSessionQuestionUseCase,
    val finishSession: FinishPreparationSessionUseCase,
    val getSessionSummary: GetPreparationSessionSummaryUseCase,
)
