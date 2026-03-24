package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertTrue

class RandomHomeScreenNextInterviewTypePickerTest {

    @Test
    fun `picker returns only defined enum values`() {
        val picker = RandomHomeScreenNextInterviewTypePicker(Random(0))
        repeat(50) {
            val picked = picker.pick()
            assertTrue(
                picked in HomeScreenNextInterviewTypeDto.entries,
                "picked=$picked",
            )
        }
    }
}
