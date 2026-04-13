@file:Suppress("TooManyFunctions")

package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.api.model.`in`.DatabaseLearningFacilitatedPageQuery
import dev.kigya.headway.database.api.model.out.DatabaseLearningCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningPageResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningProgressState
import dev.kigya.headway.database.api.model.out.DatabaseLearningQuestionDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSearchResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseLearningSkillGroup
import dev.kigya.headway.database.api.model.out.DatabaseLearningTagDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.data.scope.ensureFacilitatorLearningSubjectAccess
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressState
import dev.kigya.headway.database.internal.data.table.LearningQuestionProgressTable
import dev.kigya.headway.database.internal.data.table.QuestionLocaleCode
import dev.kigya.headway.database.internal.data.table.QuestionLocalesTable
import dev.kigya.headway.database.internal.data.table.QuestionSkillGroup
import dev.kigya.headway.database.internal.data.table.QuestionTagsTable
import dev.kigya.headway.database.internal.data.table.QuestionsTable
import dev.kigya.headway.database.internal.data.table.TagsTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.util.UUID

internal class LearningQuestionsReadRepository(
    private val database: Database,
) {

    suspend fun loadPublicCatalog(locale: String): DatabaseLearningCatalogResponseDto =
        database.dbQuery {
            buildCatalog(
                preferredLocale = normalizeLocale(locale),
                subjectUserId = null,
            )
        }

    suspend fun loadCatalog(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningCatalogResponseDto = database.dbQuery {
        val subject = resolveSubjectUserId(
            facilitatorUserId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
        buildCatalog(
            preferredLocale = normalizeLocale(locale),
            subjectUserId = subject,
        )
    }

    suspend fun loadPublicPage(
        locale: String,
        skillGroup: DatabaseLearningSkillGroup,
        limit: Int,
        afterQuestionId: Long?,
    ): DatabaseLearningPageResponseDto = database.dbQuery {
        buildPage(
            preferredLocale = normalizeLocale(locale),
            skillGroup = toInternalSkillGroup(skillGroup),
            limit = limit,
            afterQuestionId = afterQuestionId,
            subjectUserId = null,
        )
    }

    suspend fun loadPage(
        query: DatabaseLearningFacilitatedPageQuery,
    ): DatabaseLearningPageResponseDto = database.dbQuery {
        val subject = resolveSubjectUserId(
            facilitatorUserId = query.facilitatorUserId,
            facilitatorRole = query.facilitatorRole,
            subjectUserId = query.subjectUserId,
        )
        buildPage(
            preferredLocale = normalizeLocale(query.locale),
            skillGroup = toInternalSkillGroup(query.skillGroup),
            limit = query.limit,
            afterQuestionId = query.afterQuestionId,
            subjectUserId = subject,
        )
    }

    suspend fun loadPublicSearch(
        locale: String,
        query: String,
    ): DatabaseLearningSearchResponseDto = database.dbQuery {
        buildSearch(
            preferredLocale = normalizeLocale(locale),
            rawQuery = query,
            subjectUserId = null,
        )
    }

    suspend fun loadSearch(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
        query: String,
        subjectUserId: UUID?,
    ): DatabaseLearningSearchResponseDto = database.dbQuery {
        val subject = resolveSubjectUserId(
            facilitatorUserId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
        buildSearch(
            preferredLocale = normalizeLocale(locale),
            rawQuery = query,
            subjectUserId = subject,
        )
    }

    suspend fun loadPublicQuestionById(
        questionId: Long,
        locale: String,
    ): DatabaseLearningQuestionDto = database.dbQuery {
        loadSingleQuestion(
            questionId = questionId,
            preferredLocale = normalizeLocale(locale),
            subjectUserId = null,
        )
    }

    suspend fun loadQuestionById(
        facilitatorUserId: UUID,
        facilitatorRole: DatabaseUserRole,
        questionId: Long,
        locale: String,
        subjectUserId: UUID?,
    ): DatabaseLearningQuestionDto = database.dbQuery {
        val subject = resolveSubjectUserId(
            facilitatorUserId = facilitatorUserId,
            facilitatorRole = facilitatorRole,
            subjectUserId = subjectUserId,
        )
        loadSingleQuestion(
            questionId = questionId,
            preferredLocale = normalizeLocale(locale),
            subjectUserId = subject,
        )
    }
}

private fun resolveSubjectUserId(
    facilitatorUserId: UUID,
    facilitatorRole: DatabaseUserRole,
    subjectUserId: UUID?,
): UUID {
    val subject = subjectUserId ?: facilitatorUserId
    ensureFacilitatorLearningSubjectAccess(
        facilitatorId = facilitatorUserId,
        facilitatorRole = facilitatorRole,
        subjectUserId = subject,
    )
    return subject
}

private fun buildCatalog(
    preferredLocale: QuestionLocaleCode,
    subjectUserId: UUID?,
): DatabaseLearningCatalogResponseDto {
    val hardRows = loadActiveQuestionRows(QuestionSkillGroup.HARD)
    val softRows = loadActiveQuestionRows(QuestionSkillGroup.SOFT)
    val hardIds = hardRows.map { row -> row[QuestionsTable.id] }
    val softIds = softRows.map { row -> row[QuestionsTable.id] }
    val progress = loadProgressMaps(subjectUserId, hardIds + softIds)
    val tagsByQuestion = loadTagDtosByQuestionId(hardIds + softIds, preferredLocale)
    val hardDtos = hardRows.map { row ->
        toQuestionDto(
            row = row,
            preferredLocale = preferredLocale,
            tagsByQuestion = tagsByQuestion,
            progress = progress,
            subjectUserId = subjectUserId,
        )
    }
    val softDtos = softRows.map { row ->
        toQuestionDto(
            row = row,
            preferredLocale = preferredLocale,
            tagsByQuestion = tagsByQuestion,
            progress = progress,
            subjectUserId = subjectUserId,
        )
    }
    val (resumeHard, resumeSoft) = if (subjectUserId == null) {
        null to null
    } else {
        resumePair(
            hardIds = hardIds,
            softIds = softIds,
            touched = progress.keys,
        )
    }
    return DatabaseLearningCatalogResponseDto(
        hardSkills = hardDtos,
        softSkills = softDtos,
        resumeHard = resumeHard,
        resumeSoft = resumeSoft,
    )
}

private fun buildPage(
    preferredLocale: QuestionLocaleCode,
    skillGroup: QuestionSkillGroup,
    limit: Int,
    afterQuestionId: Long?,
    subjectUserId: UUID?,
): DatabaseLearningPageResponseDto {
    val allRows = loadActiveQuestionRows(skillGroup)
    val allIds = allRows.map { row -> row[QuestionsTable.id] }
    val startIndex = when (afterQuestionId) {
        null -> 0
        else -> {
            val afterQuestionIndex = allIds.indexOf(afterQuestionId)
            if (afterQuestionIndex < 0) {
                return DatabaseLearningPageResponseDto(
                    items = emptyList(),
                    resumeHard = null,
                    resumeSoft = null,
                )
            }
            afterQuestionIndex + 1
        }
    }
    val sliceIds = allIds.drop(startIndex).take(limit)
    val rowsById = allRows.associateBy { row -> row[QuestionsTable.id] }
    val progress = loadProgressMaps(subjectUserId, sliceIds)
    val tagsByQuestion = loadTagDtosByQuestionId(sliceIds, preferredLocale)
    val items = sliceIds.mapNotNull { qId ->
        rowsById[qId]?.let { row ->
            toQuestionDto(
                row = row,
                preferredLocale = preferredLocale,
                tagsByQuestion = tagsByQuestion,
                progress = progress,
                subjectUserId = subjectUserId,
            )
        }
    }
    val hardIds = loadActiveQuestionRows(QuestionSkillGroup.HARD).map { it[QuestionsTable.id] }
    val softIds = loadActiveQuestionRows(QuestionSkillGroup.SOFT).map { it[QuestionsTable.id] }
    val fullProgress = loadProgressMaps(subjectUserId, hardIds + softIds)
    val (resumeHard, resumeSoft) = if (subjectUserId == null) {
        null to null
    } else {
        resumePair(
            hardIds = hardIds,
            softIds = softIds,
            touched = fullProgress.keys,
        )
    }
    return DatabaseLearningPageResponseDto(
        items = items,
        resumeHard = resumeHard,
        resumeSoft = resumeSoft,
    )
}

private fun buildSearch(
    preferredLocale: QuestionLocaleCode,
    rawQuery: String,
    subjectUserId: UUID?,
): DatabaseLearningSearchResponseDto {
    val trimmed = rawQuery.trim()
    if (trimmed.isEmpty()) {
        return DatabaseLearningSearchResponseDto(
            hardSkills = emptyList(),
            softSkills = emptyList(),
        )
    }
    val tokens = trimmed.split(TOKEN_SPLIT_REGEX).filter { it.isNotBlank() }.map { it.lowercase() }
    if (tokens.isEmpty()) {
        return DatabaseLearningSearchResponseDto(
            hardSkills = emptyList(),
            softSkills = emptyList(),
        )
    }
    val hardHits = rankedSearchHits(
        skillGroup = QuestionSkillGroup.HARD,
        tokens = tokens,
        preferredLocale = preferredLocale,
        limit = SEARCH_CAP_PER_GROUP,
    )
    val softHits = rankedSearchHits(
        skillGroup = QuestionSkillGroup.SOFT,
        tokens = tokens,
        preferredLocale = preferredLocale,
        limit = SEARCH_CAP_PER_GROUP,
    )
    val allIds = (hardHits + softHits).distinct()
    val progress = loadProgressMaps(subjectUserId, allIds)
    val tagsByQuestion = loadTagDtosByQuestionId(allIds, preferredLocale)
    val rowsById = loadRowsByIds(allIds)
    fun mapHits(ids: List<Long>) = ids.mapNotNull { qId ->
        rowsById[qId]?.let { row ->
            toQuestionDto(
                row = row,
                preferredLocale = preferredLocale,
                tagsByQuestion = tagsByQuestion,
                progress = progress,
                subjectUserId = subjectUserId,
            )
        }
    }
    return DatabaseLearningSearchResponseDto(
        hardSkills = mapHits(hardHits),
        softSkills = mapHits(softHits),
    )
}

private fun rankedSearchHits(
    skillGroup: QuestionSkillGroup,
    tokens: List<String>,
    preferredLocale: QuestionLocaleCode,
    limit: Int,
): List<Long> {
    val rows = loadActiveQuestionRows(skillGroup)
    if (rows.isEmpty()) {
        return emptyList()
    }
    val questionIds = rows.map { row -> row[QuestionsTable.id] }
    val texts = localizedQuestionTextsForQuestions(
        questionIds = questionIds,
        preferredLocale = preferredLocale,
    )
    val tagKeysByQuestion = tagKeysByQuestionIds(questionIds = questionIds)
    val scored = rows.mapNotNull { row ->
        val qId = row[QuestionsTable.id]
        val text = texts.getValue(qId).lowercase()
        val tagKeys = tagKeysByQuestion[qId].orEmpty().map { key -> key.lowercase() }
        var score = 0
        for (token in tokens) {
            if (text.contains(token)) {
                score += SEARCH_SCORE_TEXT_HIT
            }
            if (tagKeys.any { key -> key.contains(token) }) {
                score += SEARCH_SCORE_TAG_HIT
            }
        }
        if (score <= 0) {
            return@mapNotNull null
        }
        Triple(
            first = qId,
            second = score,
            third = row[QuestionsTable.displayPriority],
        )
    }
    return scored
        .sortedWith(
            compareByDescending<Triple<Long, Int, Int?>> { it.second }
                .thenBy { it.third == null }
                .thenBy { it.third ?: Int.MAX_VALUE }
                .thenBy { it.first },
        )
        .map { it.first }
        .take(limit)
}

private fun loadSingleQuestion(
    questionId: Long,
    preferredLocale: QuestionLocaleCode,
    subjectUserId: UUID?,
): DatabaseLearningQuestionDto {
    val row = QuestionsTable.selectAll()
        .where { (QuestionsTable.id eq questionId) and (QuestionsTable.isActive eq true) }
        .firstOrNull()
        ?: throw DatabaseException.NotFound("Question not found")
    val progress = loadProgressMaps(subjectUserId, listOf(questionId))
    val tagsByQuestion = loadTagDtosByQuestionId(listOf(questionId), preferredLocale)
    return toQuestionDto(
        row = row,
        preferredLocale = preferredLocale,
        tagsByQuestion = tagsByQuestion,
        progress = progress,
        subjectUserId = subjectUserId,
    )
}

private fun loadActiveQuestionRows(skillGroup: QuestionSkillGroup): List<ResultRow> =
    QuestionsTable.selectAll()
        .where {
            (QuestionsTable.isActive eq true) and (QuestionsTable.skillGroup eq skillGroup)
        }
        .toList()
        .sortedWith(learningQuestionRowComparator)

private fun loadRowsByIds(ids: List<Long>): Map<Long, ResultRow> {
    if (ids.isEmpty()) {
        return emptyMap()
    }
    val predicate: Op<Boolean> = ids.toDisjunction { qId -> QuestionsTable.id eq qId }
    return QuestionsTable.selectAll()
        .where { predicate and (QuestionsTable.isActive eq true) }
        .associateBy { r -> r[QuestionsTable.id] }
}

private fun loadProgressMaps(
    subjectUserId: UUID?,
    questionIds: List<Long>,
): Map<Long, DatabaseLearningProgressState> {
    if (subjectUserId == null || questionIds.isEmpty()) {
        return emptyMap()
    }
    val predicate: Op<Boolean> = questionIds.toDisjunction { qId ->
        LearningQuestionProgressTable.questionId eq qId
    }
    return LearningQuestionProgressTable.selectAll()
        .where {
            (LearningQuestionProgressTable.subjectUserId eq subjectUserId) and predicate
        }
        .associate { row ->
            row[LearningQuestionProgressTable.questionId] to toWireProgressState(
                row[LearningQuestionProgressTable.state],
            )
        }
}

private fun toQuestionDto(
    row: ResultRow,
    preferredLocale: QuestionLocaleCode,
    tagsByQuestion: Map<Long, List<DatabaseLearningTagDto>>,
    progress: Map<Long, DatabaseLearningProgressState>,
    subjectUserId: UUID?,
): DatabaseLearningQuestionDto {
    val qId = row[QuestionsTable.id]
    val skillGroup = toWireSkillGroup(row[QuestionsTable.skillGroup])
    val text = localizedQuestionText(questionId = qId, preferredLocale = preferredLocale)
    val progressValue = when (subjectUserId) {
        null -> null
        else -> progress[qId] ?: DatabaseLearningProgressState.NOT_ANSWERED
    }
    return DatabaseLearningQuestionDto(
        id = qId,
        skillGroup = skillGroup,
        text = text,
        tags = tagsByQuestion[qId].orEmpty(),
        progress = progressValue,
    )
}

private fun localizedQuestionText(
    questionId: Long,
    preferredLocale: QuestionLocaleCode,
): String {
    val rows = QuestionLocalesTable.selectAll()
        .where { QuestionLocalesTable.questionId eq questionId }
        .associate { r -> r[QuestionLocalesTable.locale] to r[QuestionLocalesTable.questionText] }
    rows[preferredLocale]?.let { return it }
    rows[QuestionLocaleCode.EN]?.let { return it }
    rows[QuestionLocaleCode.RU]?.let { return it }
    return "Question $questionId"
}

private fun localizedQuestionTextsForQuestions(
    questionIds: List<Long>,
    preferredLocale: QuestionLocaleCode,
): Map<Long, String> {
    if (questionIds.isEmpty()) {
        return emptyMap()
    }
    val qp: Op<Boolean> = questionIds.toDisjunction { qId -> QuestionLocalesTable.questionId eq qId }
    val localeRows = QuestionLocalesTable.selectAll().where { qp }.toList()
    val byQuestion = localeRows.groupBy { r -> r[QuestionLocalesTable.questionId] }
        .mapValues { (_, rows) ->
            rows.associate { r -> r[QuestionLocalesTable.locale] to r[QuestionLocalesTable.questionText] }
        }
    return questionIds.distinct().associateWith { qId ->
        val localeMap = byQuestion[qId] ?: emptyMap()
        localeMap[preferredLocale]
            ?: localeMap[QuestionLocaleCode.EN]
            ?: localeMap[QuestionLocaleCode.RU]
            ?: "Question $qId"
    }
}

private fun loadTagDtosByQuestionId(
    questionIds: List<Long>,
    preferredLocale: QuestionLocaleCode,
): Map<Long, List<DatabaseLearningTagDto>> {
    if (questionIds.isEmpty()) {
        return emptyMap()
    }
    val qp: Op<Boolean> = questionIds.toDisjunction { qId -> QuestionTagsTable.questionId eq qId }
    val links = QuestionTagsTable.selectAll().where { qp }.toList()
    if (links.isEmpty()) {
        return emptyMap()
    }
    val tagIds = links.map { l -> l[QuestionTagsTable.tagId] }.distinct()
    val tp: Op<Boolean> = tagIds.toDisjunction { tid -> TagsTable.id eq tid }
    val tagKeys = TagsTable.selectAll().where { tp }.associate { r ->
        r[TagsTable.id] to r[TagsTable.key]
    }
    return links.groupBy { l -> l[QuestionTagsTable.questionId] }
        .mapValues { (_, linkRows) ->
            linkRows.mapNotNull { link ->
                val tid = link[QuestionTagsTable.tagId]
                val key = tagKeys[tid] ?: return@mapNotNull null
                DatabaseLearningTagDto(id = tid, label = key)
            }
        }
}

private fun tagKeysByQuestionIds(questionIds: List<Long>): Map<Long, List<String>> {
    if (questionIds.isEmpty()) {
        return emptyMap()
    }
    val qp: Op<Boolean> = questionIds.toDisjunction { qId -> QuestionTagsTable.questionId eq qId }
    val links = QuestionTagsTable.selectAll().where { qp }.toList()
    if (links.isEmpty()) {
        return emptyMap()
    }
    val tagIds = links.map { l -> l[QuestionTagsTable.tagId] }.distinct()
    val tp: Op<Boolean> = tagIds.toDisjunction { tid -> TagsTable.id eq tid }
    val idToKey = TagsTable.selectAll().where { tp }.associate { r ->
        r[TagsTable.id] to r[TagsTable.key]
    }
    return links.groupBy { l -> l[QuestionTagsTable.questionId] }
        .mapValues { (_, linkRows) ->
            linkRows.mapNotNull { link -> idToKey[link[QuestionTagsTable.tagId]] }
        }
}

private fun resumePair(
    hardIds: List<Long>,
    softIds: List<Long>,
    touched: Set<Long>,
): Pair<Long?, Long?> =
    longestTouchedPrefixAnchor(hardIds, touched) to longestTouchedPrefixAnchor(softIds, touched)

private fun longestTouchedPrefixAnchor(
    orderedIds: List<Long>,
    touched: Set<Long>,
): Long? {
    var last: Long? = null
    for (id in orderedIds) {
        if (id !in touched) {
            break
        }
        last = id
    }
    return last
}

private val learningQuestionRowComparator: Comparator<ResultRow> =
    compareBy<ResultRow> { row ->
        row[QuestionsTable.displayPriority] == null
    }.thenBy { row ->
        row[QuestionsTable.displayPriority] ?: Int.MAX_VALUE
    }.thenBy { row ->
        row[QuestionsTable.id]
    }

private fun normalizeLocale(locale: String): QuestionLocaleCode {
    val trimmed = locale.trim().lowercase()
    return when {
        trimmed.startsWith("ru") -> QuestionLocaleCode.RU
        else -> QuestionLocaleCode.EN
    }
}

private fun toInternalSkillGroup(group: DatabaseLearningSkillGroup): QuestionSkillGroup =
    when (group) {
        DatabaseLearningSkillGroup.HARD -> QuestionSkillGroup.HARD
        DatabaseLearningSkillGroup.SOFT -> QuestionSkillGroup.SOFT
    }

private fun toWireSkillGroup(group: QuestionSkillGroup): DatabaseLearningSkillGroup = when (group) {
    QuestionSkillGroup.HARD -> DatabaseLearningSkillGroup.HARD
    QuestionSkillGroup.SOFT -> DatabaseLearningSkillGroup.SOFT
}

private fun toWireProgressState(
    state: LearningQuestionProgressState,
): DatabaseLearningProgressState = when (state) {
    LearningQuestionProgressState.NOT_ANSWERED -> DatabaseLearningProgressState.NOT_ANSWERED
    LearningQuestionProgressState.PARTIALLY_ANSWERED -> DatabaseLearningProgressState.PARTIALLY_ANSWERED
    LearningQuestionProgressState.ANSWERED -> DatabaseLearningProgressState.ANSWERED
}

private val TOKEN_SPLIT_REGEX: Regex = "\\s+".toRegex()

private const val SEARCH_CAP_PER_GROUP: Int = 25

private const val SEARCH_SCORE_TEXT_HIT: Int = 2

private const val SEARCH_SCORE_TAG_HIT: Int = 3
