package dev.kigya.headway.database.internal.domain.usecase

import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.internal.data.repository.BankQuestionSnapshotRow
import dev.kigya.headway.database.internal.data.repository.InterviewQuestionBankRepository

internal class BuildSessionQuestionSnapshotUseCase(
    private val questionBankRepository: InterviewQuestionBankRepository,
) {
    operator fun invoke(
        formatCode: DatabasePreparationFormatCode,
        preparationLanguage: String,
    ): List<BankQuestionSnapshotRow> = questionBankRepository.loadSnapshotRows(
        formatCode = formatCode,
        preparationLanguage = preparationLanguage,
    )
}
