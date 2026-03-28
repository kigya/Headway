package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.api.model.out.DatabaseLearningRemarkDto
import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.data.table.LearningQuestionPartialRemarkTable
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressState
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

internal class LearningQuestionRemarksRepository(
    private val database: Database,
) {

    suspend fun listRemarks(
        questionId: Long,
        subjectUserId: UUID,
    ): List<DatabaseLearningRemarkDto> = database.dbQuery {
        val progress = LearningQuestionProgressTable.selectAll()
            .where {
                (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and
                    (LearningQuestionProgressTable.questionId eq questionId)
            }
            .firstOrNull()
            ?.get(LearningQuestionProgressTable.state)
        if (progress != LearningQuestionProgressState.PARTIALLY_ANSWERED) {
            return@dbQuery emptyList()
        }
        LearningQuestionPartialRemarkTable.selectAll()
            .where {
                (LearningQuestionPartialRemarkTable.subjectUserId eq subjectUserId) and
                    (LearningQuestionPartialRemarkTable.questionId eq questionId)
            }
            .orderBy(LearningQuestionPartialRemarkTable.createdAt to SortOrder.ASC)
            .map { row ->
                DatabaseLearningRemarkDto(
                    id = row[LearningQuestionPartialRemarkTable.id].value,
                    authorUserId = row[LearningQuestionPartialRemarkTable.authorUserId].value,
                    body = row[LearningQuestionPartialRemarkTable.body],
                    createdAtEpochMillis = row[LearningQuestionPartialRemarkTable.createdAt]
                        .toInstant()
                        .toEpochMilli(),
                )
            }
    }

    suspend fun insertRemark(
        questionId: Long,
        subjectUserId: UUID,
        authorUserId: UUID,
        body: String,
    ): DatabaseLearningRemarkDto = database.dbQuery {
        val progress = LearningQuestionProgressTable.selectAll()
            .where {
                (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and
                    (LearningQuestionProgressTable.questionId eq questionId)
            }
            .firstOrNull()
            ?.get(LearningQuestionProgressTable.state)
            ?: throw DatabaseException.InvalidRequest("Learning progress required before remarks")
        if (progress != LearningQuestionProgressState.PARTIALLY_ANSWERED) {
            throw DatabaseException.InvalidRequest("Remarks are only available for partially answered questions")
        }
        val remarkId = UUID.randomUUID()
        LearningQuestionPartialRemarkTable.insert {
            it[id] = remarkId
            it[LearningQuestionPartialRemarkTable.subjectUserId] = subjectUserId
            it[LearningQuestionPartialRemarkTable.questionId] = questionId
            it[LearningQuestionPartialRemarkTable.authorUserId] = authorUserId
            it[LearningQuestionPartialRemarkTable.body] = body
            it[LearningQuestionPartialRemarkTable.createdAt] = CurrentTimestampWithTimeZone
        }
        val row = LearningQuestionPartialRemarkTable.selectAll()
            .where { LearningQuestionPartialRemarkTable.id eq remarkId }
            .single()
        DatabaseLearningRemarkDto(
            id = row[LearningQuestionPartialRemarkTable.id].value,
            authorUserId = row[LearningQuestionPartialRemarkTable.authorUserId].value,
            body = row[LearningQuestionPartialRemarkTable.body],
            createdAtEpochMillis = row[LearningQuestionPartialRemarkTable.createdAt]
                .toInstant()
                .toEpochMilli(),
        )
    }
}
