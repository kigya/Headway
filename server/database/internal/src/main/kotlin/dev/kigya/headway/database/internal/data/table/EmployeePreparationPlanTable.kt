package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.timestampWithTimeZone
import java.time.OffsetDateTime

internal object EmployeePreparationPlanTable : Table(name = "public.employee_preparation_plan") {
    val userId = reference(
        name = "user_id",
        foreign = UsersTable,
        onDelete = ReferenceOption.CASCADE,
    )
    val readinessPercent = short(name = "readiness_percent").nullable()
    val recommendedFormatCode = text(name = "recommended_format_code")
    val updatedAt = timestampWithTimeZone(name = "updated_at")
        .clientDefault { OffsetDateTime.now() }

    override val primaryKey = PrimaryKey(userId)
}
