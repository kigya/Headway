package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import dev.kigya.headway.database.internal.data.table.QuestionLocaleCode
import dev.kigya.headway.database.internal.data.table.QuestionLocalesTable
import dev.kigya.headway.database.internal.data.table.QuestionSkillGroup
import dev.kigya.headway.database.internal.data.table.QuestionTagsTable
import dev.kigya.headway.database.internal.data.table.QuestionTrack
import dev.kigya.headway.database.internal.data.table.QuestionsTable
import dev.kigya.headway.database.internal.data.table.TagsTable
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.selectAll

internal data class BankQuestionSnapshotRow(
    val sourceQuestionId: Long,
    val topicTagKeys: List<String>,
    val domain: DatabasePreparationQuestionDomain?,
    val titleOrPrompt: String,
    val body: String?,
)

internal class InterviewQuestionBankRepository {

    fun loadSnapshotRows(
        formatCode: DatabasePreparationFormatCode,
        preparationLanguage: String,
    ): List<BankQuestionSnapshotRow> {
        val preferredLocale = resolveLocaleCode(preparationLanguage)
        val trackFilter = tracksForFormat(formatCode)
        val activePredicate = QuestionsTable.isActive eq true
        val trackPredicate: Op<Boolean> = trackFilter
            ?.toDisjunction { track -> QuestionsTable.track eq track }
            ?: Op.TRUE
        val questionRows = QuestionsTable.selectAll()
            .where { activePredicate and trackPredicate }
            .toList()
            .sortedWith(questionRowComparator)
        if (questionRows.isEmpty()) {
            return emptyList()
        }
        val questionIds = questionRows.map { row -> row[QuestionsTable.id] }
        val tagsByQuestion = loadTagKeysByQuestionId(questionIds)
        return questionRows.map { row ->
            val qId = row[QuestionsTable.id]
            val (title, body) = resolveLocaleTexts(
                questionId = qId,
                preferredLocale = preferredLocale,
            )
            BankQuestionSnapshotRow(
                sourceQuestionId = qId,
                topicTagKeys = tagsByQuestion[qId].orEmpty(),
                domain = toQuestionDomain(row[QuestionsTable.skillGroup]),
                titleOrPrompt = title,
                body = body,
            )
        }
    }

    private fun loadTagKeysByQuestionId(questionIds: List<Long>): Map<Long, List<String>> {
        if (questionIds.isEmpty()) {
            return emptyMap()
        }
        val questionPredicate = questionIds.toDisjunction { qId -> QuestionTagsTable.questionId eq qId }
        val links = QuestionTagsTable.selectAll()
            .where { questionPredicate }
            .toList()
        if (links.isEmpty()) {
            return emptyMap()
        }
        val tagIds = links.map { row -> row[QuestionTagsTable.tagId] }.distinct()
        val tagPredicate = tagIds.toDisjunction { tagId -> TagsTable.id eq tagId }
        val tagKeysById = TagsTable.selectAll()
            .where { tagPredicate }
            .associate { row -> row[TagsTable.id] to row[TagsTable.key] }
        return links.groupBy { row -> row[QuestionTagsTable.questionId] }
            .mapValues { (_, rows) ->
                rows.mapNotNull { row -> tagKeysById[row[QuestionTagsTable.tagId]] }
            }
    }

    private fun resolveLocaleTexts(
        questionId: Long,
        preferredLocale: QuestionLocaleCode,
    ): Pair<String, String?> {
        val preferredRow = QuestionLocalesTable.selectAll()
            .where {
                (QuestionLocalesTable.questionId eq questionId) and
                    (QuestionLocalesTable.locale eq preferredLocale)
            }
            .firstOrNull()
        if (preferredRow != null) {
            return preferredRow[QuestionLocalesTable.questionText] to preferredRow[QuestionLocalesTable.aiAnswer]
        }
        val fallbackOrder = when (preferredLocale) {
            QuestionLocaleCode.EN -> listOf(QuestionLocaleCode.RU)
            QuestionLocaleCode.RU -> listOf(QuestionLocaleCode.EN)
        }
        for (locale in fallbackOrder) {
            val row = QuestionLocalesTable.selectAll()
                .where {
                    (QuestionLocalesTable.questionId eq questionId) and
                        (QuestionLocalesTable.locale eq locale)
                }
                .firstOrNull()
            if (row != null) {
                return row[QuestionLocalesTable.questionText] to row[QuestionLocalesTable.aiAnswer]
            }
        }
        return "Question $questionId" to null
    }
}

internal fun <T> List<T>.toDisjunction(toPredicate: (T) -> Op<Boolean>): Op<Boolean> {
    val first = firstOrNull() ?: return Op.FALSE
    return drop(1).fold(toPredicate(first)) { acc, item -> acc or toPredicate(item) }
}

private val questionRowComparator: Comparator<ResultRow> =
    compareBy<ResultRow> { row ->
        row[QuestionsTable.displayPriority] == null
    }.thenBy { row ->
        row[QuestionsTable.displayPriority] ?: Int.MAX_VALUE
    }.thenBy { row ->
        row[QuestionsTable.id]
    }

private fun resolveLocaleCode(preparationLanguage: String): QuestionLocaleCode {
    val normalized = preparationLanguage.trim().lowercase()
    return when (normalized) {
        "ru", "rus" -> QuestionLocaleCode.RU
        else -> QuestionLocaleCode.EN
    }
}

private fun tracksForFormat(formatCode: DatabasePreparationFormatCode): List<QuestionTrack>? =
    when (formatCode) {
        DatabasePreparationFormatCode.CHECK -> listOf(QuestionTrack.FAST_TRACK)
        DatabasePreparationFormatCode.SPOT -> listOf(QuestionTrack.GROWTH_TRACK)
        DatabasePreparationFormatCode.MOCK -> listOf(QuestionTrack.DEEP_TRACK)
        DatabasePreparationFormatCode.SOFT_SKILLS -> listOf(QuestionTrack.SOFT_SKILLS)
        DatabasePreparationFormatCode.CUSTOM -> null
    }

private fun toQuestionDomain(skillGroup: QuestionSkillGroup): DatabasePreparationQuestionDomain? =
    when (skillGroup) {
        QuestionSkillGroup.HARD -> DatabasePreparationQuestionDomain.HARD
        QuestionSkillGroup.SOFT -> DatabasePreparationQuestionDomain.SOFT
    }
