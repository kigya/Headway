package dev.kigya.headway.database.internal.data.repository

import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogItemDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationCatalogResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationEmployeeDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationFormatCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationOutcomeCode
import dev.kigya.headway.database.api.model.out.DatabasePreparationQuestionDomain
import dev.kigya.headway.database.api.model.out.DatabasePreparationReadinessResponseDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionQuestionDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStateDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionStatus
import dev.kigya.headway.database.api.model.out.DatabasePreparationSessionSummaryDto
import dev.kigya.headway.database.api.model.out.DatabasePreparationStartSessionResponseDto
import dev.kigya.headway.database.api.model.out.DatabaseUserRole
import dev.kigya.headway.database.internal.core.extension.dbQuery
import dev.kigya.headway.database.internal.data.model.ExposedAccountStatus
import dev.kigya.headway.database.internal.data.table.EmployeePreparationPlanTable
import dev.kigya.headway.database.internal.data.table.MentorshipsTable
import dev.kigya.headway.database.internal.data.table.PreparationFormatCatalogTable
import dev.kigya.headway.database.internal.data.table.PreparationSessionTable
import dev.kigya.headway.database.internal.data.table.SessionQuestionOutcomeTable
import dev.kigya.headway.database.internal.data.table.SessionQuestionTable
import dev.kigya.headway.database.internal.data.table.UsersTable
import dev.kigya.headway.database.internal.domain.error.DatabaseException
import dev.kigya.headway.database.internal.domain.repository.PreparationRepositoryContract
import dev.kigya.headway.database.internal.domain.usecase.BuildSessionQuestionSnapshotUseCase
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.v1.core.Op
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greater
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.neq
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.datetime.CurrentTimestampWithTimeZone
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import java.util.UUID

internal class PreparationRepository(
    private val database: Database,
    private val buildSessionQuestionSnapshot: BuildSessionQuestionSnapshotUseCase,
) : PreparationRepositoryContract {

    override suspend fun listSetupEmployees(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabasePreparationEmployeeDto> = database.dbQuery {
        listSetupEmployeesTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
        )
    }

    override suspend fun getEmployeeReadiness(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): DatabasePreparationReadinessResponseDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        if (!isSubjectInSetupScopeTx(facilitatorId, facilitatorRole, subjectUserId)) {
            throw DatabaseException.PreparationForbidden()
        }
        val planRow = EmployeePreparationPlanTable.selectAll()
            .where { EmployeePreparationPlanTable.userId eq subjectUserId }
            .firstOrNull()
        val recommendedCode = planRow?.let { row ->
            parseFormatCodeOrNull(row[EmployeePreparationPlanTable.recommendedFormatCode])
        } ?: DatabasePreparationFormatCode.MOCK
        val labelRow = PreparationFormatCatalogTable.selectAll()
            .where {
                (PreparationFormatCatalogTable.formatCode eq recommendedCode.name) and
                    (PreparationFormatCatalogTable.locale eq DEFAULT_CATALOG_LOCALE)
            }
            .firstOrNull()
        DatabasePreparationReadinessResponseDto(
            readinessPercent = planRow?.get(EmployeePreparationPlanTable.readinessPercent),
            recommendedFormatCode = recommendedCode,
            recommendedLabel = labelRow?.get(PreparationFormatCatalogTable.displayName),
        )
    }

    override suspend fun listFormatCatalog(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        locale: String,
    ): DatabasePreparationCatalogResponseDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        val normalizedLocale = normalizeCatalogLocale(locale)
        val rows = PreparationFormatCatalogTable.selectAll()
            .where { PreparationFormatCatalogTable.locale eq normalizedLocale }
            .toList()
        if (rows.isEmpty()) {
            throw DatabaseException.InvalidRequest("Unsupported catalog locale")
        }
        val items = rows.map { row ->
            val code = parseFormatCodeOrNull(row[PreparationFormatCatalogTable.formatCode])
                ?: return@map null
            DatabasePreparationCatalogItemDto(
                formatCode = code,
                displayName = row[PreparationFormatCatalogTable.displayName],
                shortDescription = row[PreparationFormatCatalogTable.shortDescription],
                typeTags = parseStringListJson(row[PreparationFormatCatalogTable.typeTagsJson]),
                defaultPreparationLanguage = row[PreparationFormatCatalogTable.defaultPreparationLanguage],
                selectablePreparationLanguages = if (code == DatabasePreparationFormatCode.CUSTOM) {
                    parseStringListJson(row[PreparationFormatCatalogTable.customAllowedLanguagesJson] ?: "")
                } else {
                    emptyList()
                },
            )
        }.filterNotNull()
        DatabasePreparationCatalogResponseDto(items = items)
    }

    override suspend fun startSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
        formatCode: DatabasePreparationFormatCode,
        preparationLanguage: String,
        customDomainFilter: DatabasePreparationQuestionDomain?,
    ): DatabasePreparationStartSessionResponseDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        if (!isSubjectInSetupScopeTx(facilitatorId, facilitatorRole, subjectUserId)) {
            throw DatabaseException.PreparationForbidden()
        }
        val snapshotRows = buildSessionQuestionSnapshot(
            formatCode = formatCode,
            preparationLanguage = preparationLanguage,
        )
        val sessionId = UUID.randomUUID()
        try {
            PreparationSessionTable.insert {
                it[id] = sessionId
                it[PreparationSessionTable.facilitatorUserId] = facilitatorId
                it[PreparationSessionTable.subjectUserId] = subjectUserId
                it[PreparationSessionTable.formatCode] = formatCode.name
                it[PreparationSessionTable.preparationLanguage] = preparationLanguage
                it[PreparationSessionTable.status] = STATUS_ACTIVE
                it[PreparationSessionTable.startedAt] = CurrentTimestampWithTimeZone
                it[PreparationSessionTable.endedAt] = null
                it[PreparationSessionTable.currentQuestionId] = null
                it[PreparationSessionTable.lastActiveQuestionId] = null
                it[PreparationSessionTable.customDomainFilter] = customDomainFilter?.name
            }
        } catch (e: ExposedSQLException) {
            if (isUniqueActiveSessionViolation(e)) {
                throw DatabaseException.PreparationConflict("Active preparation session already exists for this pair")
            }
            throw e
        }
        val questionIds = mutableListOf<UUID>()
        snapshotRows.forEachIndexed { index, row ->
            val qId = UUID.randomUUID()
            questionIds.add(qId)
            SessionQuestionTable.insert {
                it[id] = qId
                it[SessionQuestionTable.sessionId] = sessionId
                it[SessionQuestionTable.sortOrder] = index
                it[SessionQuestionTable.sourceQuestionId] = row.sourceQuestionId
                it[SessionQuestionTable.topicTagsJson] = topicTagKeysToJson(row.topicTagKeys)
                it[SessionQuestionTable.domain] = row.domain?.name
                it[SessionQuestionTable.titleOrPrompt] = row.titleOrPrompt
                it[SessionQuestionTable.body] = row.body
            }
        }
        val firstQuestionId = questionIds.firstOrNull()
        PreparationSessionTable.update({ PreparationSessionTable.id eq sessionId }) {
            it[currentQuestionId] = firstQuestionId
            it[lastActiveQuestionId] = firstQuestionId
        }
        val state = loadSessionStateTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            sessionId = sessionId,
        )
        DatabasePreparationStartSessionResponseDto(
            sessionId = sessionId,
            state = state,
        )
    }

    override suspend fun getSessionState(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = database.dbQuery {
        loadSessionStateTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            sessionId = sessionId,
        )
    }

    override suspend fun submitOutcome(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        sessionQuestionId: UUID,
        outcome: DatabasePreparationOutcomeCode,
        comment: String?,
    ): DatabasePreparationSessionStateDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        val sessionRow = loadSessionRowForFacilitator(sessionId, facilitatorId)
        val formatCode = parseFormatCodeOrNull(sessionRow[PreparationSessionTable.formatCode])
            ?: throw DatabaseException.InvalidRequest("Unknown session format")
        when (parseSessionStatus(sessionRow[PreparationSessionTable.status])) {
            DatabasePreparationSessionStatus.COMPLETED ->
                throw DatabaseException.PreparationConflict("Session already completed")

            DatabasePreparationSessionStatus.ACTIVE ->
                ensureActiveSubjectScopeOrAutoComplete(
                    sessionRow = sessionRow,
                    facilitatorId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                )
        }
        if (outcome == DatabasePreparationOutcomeCode.PARTIAL) {
            val text = comment?.trim().orEmpty()
            if (text.length > COMMENT_MAX_LENGTH) {
                throw DatabaseException.InvalidRequest("Comment exceeds maximum length")
            }
        } else if (!comment.isNullOrBlank()) {
            throw DatabaseException.InvalidRequest("Comment is only allowed for partial outcome")
        }
        val questionRow = SessionQuestionTable.selectAll()
            .where {
                (SessionQuestionTable.id eq sessionQuestionId) and
                    (SessionQuestionTable.sessionId eq sessionId)
            }
            .firstOrNull()
            ?: throw DatabaseException.NotFound("Session question not found")
        upsertOutcomeTx(
            sessionId = sessionId,
            sessionQuestionId = sessionQuestionId,
            outcome = outcome,
            comment = comment?.trim()?.takeIf(String::isNotEmpty),
        )
        when (formatCode) {
            DatabasePreparationFormatCode.CUSTOM ->
                PreparationSessionTable.update({ PreparationSessionTable.id eq sessionId }) {
                    it[currentQuestionId] = null
                    it[lastActiveQuestionId] = sessionQuestionId
                }

            else -> {
                val nextId = findNextQuestionId(sessionId, questionRow[SessionQuestionTable.sortOrder])
                PreparationSessionTable.update({ PreparationSessionTable.id eq sessionId }) {
                    it[currentQuestionId] = nextId
                    if (nextId != null) {
                        it[lastActiveQuestionId] = nextId
                    }
                }
            }
        }
        loadSessionStateTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            sessionId = sessionId,
        )
    }

    override suspend fun selectQuestion(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        sessionQuestionId: UUID,
    ): DatabasePreparationSessionStateDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        val sessionRow = loadSessionRowForFacilitator(sessionId, facilitatorId)
        val formatCode = parseFormatCodeOrNull(sessionRow[PreparationSessionTable.formatCode])
            ?: throw DatabaseException.InvalidRequest("Unknown session format")
        if (formatCode != DatabasePreparationFormatCode.CUSTOM) {
            throw DatabaseException.InvalidRequest("Question selection is only for custom sessions")
        }
        when (parseSessionStatus(sessionRow[PreparationSessionTable.status])) {
            DatabasePreparationSessionStatus.COMPLETED ->
                throw DatabaseException.PreparationConflict("Session already completed")

            DatabasePreparationSessionStatus.ACTIVE ->
                ensureActiveSubjectScopeOrAutoComplete(
                    sessionRow = sessionRow,
                    facilitatorId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                )
        }
        val questionRow = SessionQuestionTable.selectAll()
            .where {
                (SessionQuestionTable.id eq sessionQuestionId) and
                    (SessionQuestionTable.sessionId eq sessionId)
            }
            .firstOrNull()
            ?: throw DatabaseException.NotFound("Session question not found")
        val filter = sessionRow[PreparationSessionTable.customDomainFilter]?.let { parseDomainOrNull(it) }
        val qDomain = questionRow[SessionQuestionTable.domain]?.let { parseDomainOrNull(it) }
        if (filter != null && qDomain != null && qDomain != DatabasePreparationQuestionDomain.MIXED) {
            if (qDomain != filter) {
                throw DatabaseException.InvalidRequest("Question does not match active domain filter")
            }
        }
        PreparationSessionTable.update({ PreparationSessionTable.id eq sessionId }) {
            it[currentQuestionId] = sessionQuestionId
            it[lastActiveQuestionId] = sessionQuestionId
        }
        loadSessionStateTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            sessionId = sessionId,
        )
    }

    override suspend fun finishSession(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        val sessionRow = loadSessionRowForFacilitator(sessionId, facilitatorId)
        when (parseSessionStatus(sessionRow[PreparationSessionTable.status])) {
            DatabasePreparationSessionStatus.COMPLETED ->
                return@dbQuery loadSessionStateTx(
                    facilitatorId = facilitatorId,
                    facilitatorRole = facilitatorRole,
                    sessionId = sessionId,
                )

            DatabasePreparationSessionStatus.ACTIVE -> {
                val subjectId = sessionRow[PreparationSessionTable.subjectUserId].value
                val inScope = isSubjectInSetupScopeTx(facilitatorId, facilitatorRole, subjectId)
                if (!inScope) {
                    completeSessionTx(sessionId)
                } else {
                    completeSessionTx(sessionId)
                    val formatCode = parseFormatCodeOrNull(sessionRow[PreparationSessionTable.formatCode])
                    if (formatCode != null) {
                        recomputeEmployeePlanTx(
                            subjectUserId = subjectId,
                            completedFormat = formatCode,
                        )
                    }
                }
            }
        }
        loadSessionStateTx(
            facilitatorId = facilitatorId,
            facilitatorRole = facilitatorRole,
            sessionId = sessionId,
        )
    }

    override suspend fun getSessionSummary(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
        locale: String,
    ): DatabasePreparationSessionSummaryDto = database.dbQuery {
        ensureFacilitatorRole(facilitatorRole)
        val sessionRow = loadSessionRowForFacilitator(sessionId, facilitatorId)
        if (parseSessionStatus(sessionRow[PreparationSessionTable.status]) !=
            DatabasePreparationSessionStatus.COMPLETED
        ) {
            throw DatabaseException.PreparationForbidden(
                message = dev.kigya.headway.database.api.model.DatabasePreparationErrorCodes
                    .SUMMARY_REQUIRES_COMPLETED_SESSION,
            )
        }
        val started = sessionRow[PreparationSessionTable.startedAt]
        val ended = sessionRow[PreparationSessionTable.endedAt]
        val durationSeconds = java.time.Duration.between(started, ended).seconds.coerceAtLeast(0)
        val normalizedLocale = normalizeCatalogLocale(locale)
        val (headline, body) = summaryCopy(normalizedLocale)
        DatabasePreparationSessionSummaryDto(
            sessionId = sessionId,
            durationSeconds = durationSeconds,
            headline = headline,
            body = body,
        )
    }

    private fun listSetupEmployeesTx(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ): List<DatabasePreparationEmployeeDto> {
        ensureFacilitatorRole(facilitatorRole)
        val baseFilter: Op<Boolean> =
            (UsersTable.id neq facilitatorId) and
                (UsersTable.isActive eq true) and
                (UsersTable.role eq DatabaseUserRole.EMPLOYEE) and
                (UsersTable.status eq ExposedAccountStatus.ACTIVE)

        return when (facilitatorRole) {
            DatabaseUserRole.DEVELOPER ->
                UsersTable.selectAll().where { baseFilter }

            DatabaseUserRole.MANAGER -> {
                val facilitatorRow = UsersTable.selectAll()
                    .where { UsersTable.id eq facilitatorId }
                    .singleOrNull()
                    ?: throw DatabaseException.NotFound("Facilitator not found")
                val department = facilitatorRow[UsersTable.department]
                UsersTable.selectAll().where {
                    baseFilter and (UsersTable.department eq department)
                }
            }

            DatabaseUserRole.MENTOR -> {
                val menteeIds = MentorshipsTable.selectAll()
                    .where {
                        (MentorshipsTable.mentorId eq facilitatorId) and MentorshipsTable.revokedAt.isNull()
                    }
                    .map { row -> row[MentorshipsTable.menteeId].value }
                if (menteeIds.isEmpty()) {
                    return emptyList()
                }
                val menteePredicate: Op<Boolean> = menteeIds
                    .map { menteeId -> UsersTable.id eq menteeId }
                    .reduce { acc, predicate -> acc or predicate }
                UsersTable.selectAll().where {
                    baseFilter and menteePredicate
                }
            }

            DatabaseUserRole.EMPLOYEE,
            DatabaseUserRole.GUEST,
            -> throw DatabaseException.PreparationForbidden()
        }.map { row ->
            DatabasePreparationEmployeeDto(
                id = row[UsersTable.id].value,
                displayName = row[UsersTable.fullName] ?: row[UsersTable.email],
                department = row[UsersTable.department],
                avatarUrl = row[UsersTable.avatarUrl],
            )
        }
    }

    private fun loadSessionRowForFacilitator(
        sessionId: UUID,
        facilitatorId: UUID,
    ): ResultRow = PreparationSessionTable.selectAll()
        .where { PreparationSessionTable.id eq sessionId }
        .firstOrNull()
        ?.takeIf { row -> row[PreparationSessionTable.facilitatorUserId].value == facilitatorId }
        ?: throw DatabaseException.NotFound("Preparation session not found")

    private fun loadSessionStateTx(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        sessionId: UUID,
    ): DatabasePreparationSessionStateDto {
        ensureFacilitatorRole(facilitatorRole)
        val sessionRow = loadSessionRowForFacilitator(sessionId, facilitatorId)
        val subjectId = sessionRow[PreparationSessionTable.subjectUserId].value
        val subjectRow = UsersTable.selectAll()
            .where { UsersTable.id eq subjectId }
            .firstOrNull()
            ?: throw DatabaseException.NotFound("Subject not found")
        val formatCode = parseFormatCodeOrNull(sessionRow[PreparationSessionTable.formatCode])
            ?: DatabasePreparationFormatCode.CUSTOM
        val questions = SessionQuestionTable.selectAll()
            .where { SessionQuestionTable.sessionId eq sessionId }
            .orderBy(SessionQuestionTable.sortOrder to SortOrder.ASC)
            .toList()
        val outcomes = SessionQuestionOutcomeTable.selectAll()
            .where { SessionQuestionOutcomeTable.sessionId eq sessionId }
            .associateBy { row -> row[SessionQuestionOutcomeTable.sessionQuestionId].value }
        val questionDtos = questions.map { qRow ->
            val qId = qRow[SessionQuestionTable.id].value
            val outcomeRow = outcomes[qId]
            val outcomeCode = outcomeRow?.let { r ->
                parseOutcomeOrNull(r[SessionQuestionOutcomeTable.outcome])
            }
            val addressed = outcomeCode != null
            DatabasePreparationSessionQuestionDto(
                id = qId,
                sortOrder = qRow[SessionQuestionTable.sortOrder],
                topicTags = parseStringListJson(qRow[SessionQuestionTable.topicTagsJson]),
                domain = qRow[SessionQuestionTable.domain]?.let { parseDomainOrNull(it) },
                titleOrPrompt = qRow[SessionQuestionTable.titleOrPrompt],
                body = qRow[SessionQuestionTable.body],
                outcome = outcomeCode,
                comment = outcomeRow?.let { r -> r[SessionQuestionOutcomeTable.comment] },
                addressedForUi = addressed,
            )
        }
        return DatabasePreparationSessionStateDto(
            sessionId = sessionId,
            status = parseSessionStatus(sessionRow[PreparationSessionTable.status]),
            formatCode = formatCode,
            preparationLanguage = sessionRow[PreparationSessionTable.preparationLanguage],
            facilitatorUserId = sessionRow[PreparationSessionTable.facilitatorUserId].value,
            subjectUserId = subjectId,
            subjectDisplayName = subjectRow[UsersTable.fullName] ?: subjectRow[UsersTable.email],
            subjectAvatarUrl = subjectRow[UsersTable.avatarUrl],
            currentQuestionId = sessionRow[PreparationSessionTable.currentQuestionId],
            lastActiveQuestionId = sessionRow[PreparationSessionTable.lastActiveQuestionId],
            customDomainFilter = sessionRow[PreparationSessionTable.customDomainFilter]?.let { parseDomainOrNull(it) },
            questions = questionDtos,
            startedAtEpochMillis = sessionRow[PreparationSessionTable.startedAt]
                .toInstant()
                .toEpochMilli(),
            endedAtEpochMillis = sessionRow[PreparationSessionTable.endedAt]?.toInstant()?.toEpochMilli(),
        )
    }

    private fun ensureActiveSubjectScopeOrAutoComplete(
        sessionRow: ResultRow,
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
    ) {
        val sessionId = sessionRow[PreparationSessionTable.id].value
        val subjectId = sessionRow[PreparationSessionTable.subjectUserId].value
        if (isSubjectInSetupScopeTx(facilitatorId, facilitatorRole, subjectId)) {
            return
        }
        completeSessionTx(sessionId)
        throw DatabaseException.PreparationScopeSessionClosed()
    }

    private fun completeSessionTx(sessionId: UUID) {
        PreparationSessionTable.update({ PreparationSessionTable.id eq sessionId }) {
            it[status] = STATUS_COMPLETED
            it[endedAt] = CurrentTimestampWithTimeZone
        }
    }

    private fun upsertOutcomeTx(
        sessionId: UUID,
        sessionQuestionId: UUID,
        outcome: DatabasePreparationOutcomeCode,
        comment: String?,
    ) {
        val existing = SessionQuestionOutcomeTable.selectAll()
            .where {
                (SessionQuestionOutcomeTable.sessionId eq sessionId) and
                    (SessionQuestionOutcomeTable.sessionQuestionId eq sessionQuestionId)
            }
            .firstOrNull()
        if (existing == null) {
            SessionQuestionOutcomeTable.insert {
                it[id] = UUID.randomUUID()
                it[SessionQuestionOutcomeTable.sessionId] = sessionId
                it[SessionQuestionOutcomeTable.sessionQuestionId] = sessionQuestionId
                it[SessionQuestionOutcomeTable.outcome] = outcome.name
                it[SessionQuestionOutcomeTable.comment] = comment
                it[SessionQuestionOutcomeTable.recordedAt] = CurrentTimestampWithTimeZone
            }
        } else {
            SessionQuestionOutcomeTable.update({
                (SessionQuestionOutcomeTable.sessionId eq sessionId) and
                    (SessionQuestionOutcomeTable.sessionQuestionId eq sessionQuestionId)
            }) {
                it[SessionQuestionOutcomeTable.outcome] = outcome.name
                it[SessionQuestionOutcomeTable.comment] = comment
                it[SessionQuestionOutcomeTable.recordedAt] = CurrentTimestampWithTimeZone
            }
        }
    }

    private fun findNextQuestionId(
        sessionId: UUID,
        currentSortOrder: Int,
    ): UUID? = SessionQuestionTable.selectAll()
        .where {
            (SessionQuestionTable.sessionId eq sessionId) and
                (SessionQuestionTable.sortOrder greater currentSortOrder)
        }
        .orderBy(SessionQuestionTable.sortOrder to SortOrder.ASC)
        .firstOrNull()
        ?.get(SessionQuestionTable.id)
        ?.value

    private fun recomputeEmployeePlanTx(
        subjectUserId: UUID,
        completedFormat: DatabasePreparationFormatCode,
    ) {
        val nextFormat = nextRecommendedFormat(completedFormat)
        val existing = EmployeePreparationPlanTable.selectAll()
            .where { EmployeePreparationPlanTable.userId eq subjectUserId }
            .firstOrNull()
        if (existing == null) {
            EmployeePreparationPlanTable.insert {
                it[userId] = subjectUserId
                it[readinessPercent] = null
                it[recommendedFormatCode] = nextFormat.name
                it[updatedAt] = CurrentTimestampWithTimeZone
            }
        } else {
            EmployeePreparationPlanTable.update({ EmployeePreparationPlanTable.userId eq subjectUserId }) {
                it[recommendedFormatCode] = nextFormat.name
                it[updatedAt] = CurrentTimestampWithTimeZone
            }
        }
    }

    private fun isSubjectInSetupScopeTx(
        facilitatorId: UUID,
        facilitatorRole: DatabaseUserRole,
        subjectUserId: UUID,
    ): Boolean {
        val baseFilter: Op<Boolean> =
            (UsersTable.id eq subjectUserId) and
                (UsersTable.isActive eq true) and
                (UsersTable.role eq DatabaseUserRole.EMPLOYEE) and
                (UsersTable.status eq ExposedAccountStatus.ACTIVE)

        return when (facilitatorRole) {
            DatabaseUserRole.DEVELOPER ->
                UsersTable.selectAll().where { baseFilter }.firstOrNull() != null

            DatabaseUserRole.MANAGER -> {
                val facilitatorRow = UsersTable.selectAll()
                    .where { UsersTable.id eq facilitatorId }
                    .singleOrNull()
                    ?: return false
                val department = facilitatorRow[UsersTable.department]
                UsersTable.selectAll().where { baseFilter and (UsersTable.department eq department) }
                    .firstOrNull() != null
            }

            DatabaseUserRole.MENTOR ->
                MentorshipsTable.selectAll()
                    .where {
                        (MentorshipsTable.mentorId eq facilitatorId) and
                            MentorshipsTable.revokedAt.isNull() and
                            (MentorshipsTable.menteeId eq subjectUserId)
                    }
                    .firstOrNull() != null

            DatabaseUserRole.EMPLOYEE,
            DatabaseUserRole.GUEST,
            -> false
        }
    }

    private fun ensureFacilitatorRole(facilitatorRole: DatabaseUserRole) {
        when (facilitatorRole) {
            DatabaseUserRole.GUEST,
            DatabaseUserRole.EMPLOYEE,
            -> throw DatabaseException.PreparationForbidden()

            DatabaseUserRole.DEVELOPER,
            DatabaseUserRole.MANAGER,
            DatabaseUserRole.MENTOR,
            -> Unit
        }
    }
}

private fun summaryCopy(locale: String): Pair<String, String> = when (locale) {
    "ru" -> "Отличная работа" to "Сессия подготовки завершена. Сохранённый прогресс доступен в истории."
    else -> "Great work" to "Preparation session completed. Your saved progress remains available."
}

private fun nextRecommendedFormat(
    completed: DatabasePreparationFormatCode,
): DatabasePreparationFormatCode = when (completed) {
    DatabasePreparationFormatCode.CHECK -> DatabasePreparationFormatCode.SPOT
    DatabasePreparationFormatCode.SPOT -> DatabasePreparationFormatCode.MOCK
    DatabasePreparationFormatCode.MOCK -> DatabasePreparationFormatCode.SOFT_SKILLS
    DatabasePreparationFormatCode.SOFT_SKILLS -> DatabasePreparationFormatCode.CHECK
    DatabasePreparationFormatCode.CUSTOM -> DatabasePreparationFormatCode.MOCK
}

private fun isUniqueActiveSessionViolation(e: ExposedSQLException): Boolean {
    val msg = e.message.orEmpty() + e.cause?.message.orEmpty()
    return msg.contains("preparation_session_one_active_per_pair_idx", ignoreCase = true)
}

private fun parseStringListJson(raw: String): List<String> = runCatching {
    Json.parseToJsonElement(raw).jsonArray.map { element -> element.jsonPrimitive.content }
}.getOrDefault(emptyList())

private fun parseFormatCodeOrNull(raw: String): DatabasePreparationFormatCode? = runCatching {
    DatabasePreparationFormatCode.valueOf(raw.trim())
}.getOrNull()

private fun parseSessionStatus(raw: String): DatabasePreparationSessionStatus = when (raw.trim()) {
    STATUS_COMPLETED -> DatabasePreparationSessionStatus.COMPLETED
    else -> DatabasePreparationSessionStatus.ACTIVE
}

private fun parseOutcomeOrNull(raw: String): DatabasePreparationOutcomeCode? = runCatching {
    DatabasePreparationOutcomeCode.valueOf(raw.trim())
}.getOrNull()

private fun parseDomainOrNull(raw: String): DatabasePreparationQuestionDomain? = runCatching {
    DatabasePreparationQuestionDomain.valueOf(raw.trim())
}.getOrNull()

private fun normalizeCatalogLocale(locale: String): String {
    val trimmed = locale.trim().lowercase()
    return when {
        trimmed.startsWith("ru") -> "ru"
        else -> "en"
    }
}

private fun topicTagKeysToJson(keys: List<String>): String = buildString {
    append('[')
    keys.forEachIndexed { index, key ->
        if (index > 0) {
            append(',')
        }
        append('"')
        append(key.replace("\\", "\\\\").replace("\"", "\\\""))
        append('"')
    }
    append(']')
}

private const val STATUS_ACTIVE: String = "ACTIVE"
private const val STATUS_COMPLETED: String = "COMPLETED"
private const val DEFAULT_CATALOG_LOCALE: String = "en"
private const val COMMENT_MAX_LENGTH: Int = 256
