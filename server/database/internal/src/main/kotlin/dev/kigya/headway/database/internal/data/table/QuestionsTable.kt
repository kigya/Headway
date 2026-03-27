package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import org.postgresql.util.PGobject
import java.time.OffsetDateTime

internal enum class QuestionSkillGroup {
    HARD,
    SOFT,
}

internal enum class QuestionTrack {
    FAST_TRACK,
    GROWTH_TRACK,
    DEEP_TRACK,
    SOFT_SKILLS,
}

internal object QuestionsTable : Table(name = "public.questions") {
    val id = long(name = "id").autoIncrement()
    val skillGroup = customEnumeration(
        name = "skill_group",
        sql = "skill_group",
        fromDb = { raw -> QuestionSkillGroup.valueOf(pgEnumRaw(raw)) },
        toDb = { v ->
            PGobject().apply {
                type = "skill_group"
                value = v.name
            }
        },
    )
    val track = customEnumeration(
        name = "track",
        sql = "track_type",
        fromDb = { raw -> QuestionTrack.valueOf(pgEnumRaw(raw)) },
        toDb = { v ->
            PGobject().apply {
                type = "track_type"
                value = v.name
            }
        },
    )
    val isActive = bool(name = "is_active").clientDefault { true }
    val displayPriority = integer(name = "display_priority").nullable()
    val importSourceKey = text(name = "import_source_key").nullable()
    val createdAt = timestampWithTimeZone(name = "created_at")
        .clientDefault { OffsetDateTime.now() }
    val updatedAt = timestampWithTimeZone(name = "updated_at")
        .clientDefault { OffsetDateTime.now() }

    override val primaryKey = PrimaryKey(id)
}

internal fun pgEnumRaw(value: Any): String = (value as? PGobject)?.value ?: value.toString()
