package dev.kigya.headway.admin.internal.presentation.plugin

import kotlin.test.Test
import kotlin.test.assertEquals

class AdminOAuthPublicOriginTest {
    @Test
    fun `parsePublicOriginCandidates splits comma separated values`() {
        val parsed = parsePublicOriginCandidates(" http://localhost:8083 , https://x.test ")
        assertEquals(
            listOf("http://localhost:8083", "https://x.test"),
            parsed,
        )
    }

    @Test
    fun `parsePublicOriginCandidates returns empty for blank`() {
        assertEquals(emptyList(), parsePublicOriginCandidates(null))
        assertEquals(emptyList(), parsePublicOriginCandidates("   "))
        assertEquals(emptyList(), parsePublicOriginCandidates(",,"))
    }

    @Test
    fun `selectPublicOriginBase matches request host to candidate`() {
        val chosen = selectPublicOriginBase(
            candidates = listOf("http://127.0.0.1:8083", "http://localhost:8083"),
            requestOrigin = "http://localhost:8083",
        )
        assertEquals("http://localhost:8083", chosen)
    }

    @Test
    fun `selectPublicOriginBase treats localhost and loopback as equivalent`() {
        val chosen = selectPublicOriginBase(
            candidates = listOf("http://127.0.0.1:8083"),
            requestOrigin = "http://localhost:8083",
        )
        assertEquals("http://127.0.0.1:8083", chosen)
    }

    @Test
    fun `selectPublicOriginBase falls back to first when no match`() {
        val chosen = selectPublicOriginBase(
            candidates = listOf("https://dev.example.com", "https://other.example.com"),
            requestOrigin = "https://unrelated.example.com",
        )
        assertEquals("https://dev.example.com", chosen)
    }
}
