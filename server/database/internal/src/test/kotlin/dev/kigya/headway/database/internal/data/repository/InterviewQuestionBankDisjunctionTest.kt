package dev.kigya.headway.database.internal.data.repository

import org.jetbrains.exposed.v1.core.Op
import kotlin.test.Test
import kotlin.test.assertEquals

class InterviewQuestionBankDisjunctionTest {

    @Test
    fun `empty list disjunction is FALSE`() {
        val op: Op<Boolean> = emptyList<Int>().toDisjunction { throw AssertionError() }
        assertEquals(Op.FALSE, op)
    }

    @Test
    fun `single element returns predicate result`() {
        val op = listOf(7).toDisjunction { v ->
            assertEquals(7, v)
            Op.TRUE
        }
        assertEquals(Op.TRUE, op)
    }

    @Test
    fun `folds every element into OR chain`() {
        val calls = mutableListOf<Int>()
        listOf(10, 20, 30).toDisjunction { v ->
            calls.add(v)
            Op.FALSE
        }
        assertEquals(listOf(10, 20, 30), calls)
    }
}
