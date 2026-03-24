package dev.kigya.headway.common.util

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EnvironmentTest {
    @Test
    fun `from rejects missing blank or unknown values`() {
        assertFailsWith<IllegalArgumentException> { Environment.from(null) }
        assertFailsWith<IllegalArgumentException> { Environment.from("") }
        assertFailsWith<IllegalArgumentException> { Environment.from("   ") }
        assertFailsWith<IllegalArgumentException> { Environment.from("staging") }
    }

    @Test
    fun `from normalizes known values`() {
        assertEquals(Environment.DEV, Environment.from(" dev "))
        assertEquals(Environment.DEV, Environment.from("DEV"))
        assertEquals(Environment.PROD, Environment.from("prod"))
    }

    @Test
    fun `flags expose current environment`() {
        assertTrue(Environment.DEV.isDev)
        assertTrue(Environment.PROD.isProd)
        assertFalse(Environment.DEV.isProd)
        assertFalse(Environment.PROD.isDev)
    }
}
