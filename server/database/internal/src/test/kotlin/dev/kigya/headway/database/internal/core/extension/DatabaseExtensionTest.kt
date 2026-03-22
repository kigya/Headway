package dev.kigya.headway.database.internal.core.extension

import dev.kigya.headway.database.internal.domain.error.DatabaseException
import java.sql.SQLException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DatabaseExtensionTest {

    @Test
    fun `invite domain violation maps to invalid request`() {
        val exception = SQLException(
            """
                |ERROR: Email domain "gmail.com" is not allowed
                |  Where: PL/pgSQL function assert_allowed_domain_for_invites() line 14 at RAISE
            """.trimMargin(),
        )

        val result = exception.toDatabaseException()

        val invalidRequest = assertIs<DatabaseException.InvalidRequest>(result)
        assertEquals(
            expected = """Email domain "gmail.com" is not allowed""",
            actual = invalidRequest.message,
        )
    }

    @Test
    fun `unexpected sql exception stays dependency unavailable`() {
        val exception = SQLException("Connection refused")

        val result = exception.toDatabaseException()

        val dependencyUnavailable = assertIs<DatabaseException.DependencyUnavailable>(result)
        assertEquals(
            expected = "postgres",
            actual = dependencyUnavailable.dependency,
        )
    }
}
