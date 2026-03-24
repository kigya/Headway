package dev.kigya.headway.admin.internal.core.session

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AdminSessionTest {
    @Test
    fun `sign and verify returns original session`() {
        val session = AdminSession(
            githubLogin = "octocat",
            githubAvatarUrl = "https://avatars.githubusercontent.com/u/1",
            hasRepositoryAccess = true,
            createdAt = 1_000L,
        )

        val signedCookie = AdminSession.sign(
            session = session,
            secret = SESSION_SECRET,
        )

        val restoredSession = AdminSession.verify(
            cookieValue = signedCookie,
            secret = SESSION_SECRET,
            ttlMs = 5_000L,
            nowMs = 3_000L,
        )

        assertNotNull(restoredSession)
        assertEquals(session, restoredSession)
    }

    @Test
    fun `verify returns null for tampered cookie`() {
        val session = AdminSession(
            githubLogin = "octocat",
            hasRepositoryAccess = true,
            createdAt = 1_000L,
        )
        val signedCookie = AdminSession.sign(
            session = session,
            secret = SESSION_SECRET,
        )

        val tamperedCookie = signedCookie.replaceRange(
            startIndex = signedCookie.length - 1,
            endIndex = signedCookie.length,
            replacement = if (signedCookie.last() == 'a') "b" else "a",
        )

        val restoredSession = AdminSession.verify(
            cookieValue = tamperedCookie,
            secret = SESSION_SECRET,
            ttlMs = 5_000L,
            nowMs = 3_000L,
        )

        assertNull(restoredSession)
    }

    @Test
    fun `verify returns null for expired session`() {
        val session = AdminSession(
            githubLogin = "octocat",
            hasRepositoryAccess = true,
            createdAt = 1_000L,
        )
        val signedCookie = AdminSession.sign(
            session = session,
            secret = SESSION_SECRET,
        )

        val restoredSession = AdminSession.verify(
            cookieValue = signedCookie,
            secret = SESSION_SECRET,
            ttlMs = 500L,
            nowMs = 2_000L,
        )

        assertNull(restoredSession)
    }
}

private const val SESSION_SECRET = "test-admin-session-secret-that-is-long-enough"
