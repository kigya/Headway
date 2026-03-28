package dev.kigya.headway.database.internal.data.learning

import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import dev.kigya.headway.database.internal.data.table.LearningQuestionPartialRemarkTable
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressState
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressTable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

internal class LearningQuestionPreparationSync {

    fun recordPreparationOutcome(
        subjectUserId: UUID,
        bankQuestionId: Long,
        outcome: DatabasePreparationOutcomeCode,
    ) {
        when (outcome) {
            DatabasePreparationOutcomeCode.GOOD -> {
                upsertProgressState(subjectUserId, bankQuestionId, LearningQuestionProgressState.ANSWERED)
                LearningQuestionPartialRemarkTable.deleteWhere {
                    (LearningQuestionPartialRemarkTable.subjectUserId eq subjectUserId) and
                        (LearningQuestionPartialRemarkTable.questionId eq bankQuestionId)
                }
            }

            DatabasePreparationOutcomeCode.PARTIAL ->
                upsertProgressState(
                    subjectUserId,
                    bankQuestionId,
                    LearningQuestionProgressState.PARTIALLY_ANSWERED,
                )

            DatabasePreparationOutcomeCode.NOT_ANSWERED -> {
                LearningQuestionProgressTable.deleteWhere {
                    (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and
                        (LearningQuestionProgressTable.questionId eq bankQuestionId)
                }
                LearningQuestionPartialRemarkTable.deleteWhere {
                    (LearningQuestionPartialRemarkTable.subjectUserId eq subjectUserId) and
                        (LearningQuestionPartialRemarkTable.questionId eq bankQuestionId)
                }
            }
        }
    }

    private fun upsertProgressState(
        subjectUserId: UUID,
        questionId: Long,
        state: LearningQuestionProgressState,
    ) {
        val existing = LearningQuestionProgressTable.selectAll()
            .where {
                (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and
                    (LearningQuestionProgressTable.questionId eq questionId)
            }
            .firstOrNull()
        if (existing == null) {
            LearningQuestionProgressTable.insert {
                it[LearningQuestionProgressTable.subjectUserId] = subjectUserId
                it[LearningQuestionProgressTable.questionId] = questionId
                it[LearningQuestionProgressTable.state] = state
                it[LearningQuestionProgressTable.updatedAt] = CurrentTimestampWithTimeZone
            }
        } else {
            LearningQuestionProgressTable.update({
                (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and
                    (LearningQuestionProgressTable.questionId eq questionId)
            }) {
                it[LearningQuestionProgressTable.state] = state
                it[LearningQuestionProgressTable.updatedAt] = CurrentTimestampWithTimeZone
            }
        }
    }
}
