package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.UUIDTable
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object PreparationSessionTable : UUIDTable(name = "public.preparation_session") {
    val facilitatorUserId = reference(
        name = "facilitator_user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val subjectUserId = reference(
        name = "subject_user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val formatCode = text(name = "format_code")
    val preparationLanguage = text(name = "preparation_language")
    val status = text(name = "status")
    val startedAt = timestampWithTimeZone(name = "started_at")
        .clientDefault { OffsetDateTime.now() }
    val endedAt = timestampWithTimeZone(name = "ended_at").nullable()
    val currentQuestionId = uuid(name = "current_question_id").nullable()
    val lastActiveQuestionId = uuid(name = "last_active_question_id").nullable()
    val customDomainFilter = text(name = "custom_domain_filter").nullable()
}
