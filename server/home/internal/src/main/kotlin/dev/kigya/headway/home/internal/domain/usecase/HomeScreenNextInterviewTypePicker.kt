package dev.kigya.headway.home.internal.domain.usecase

import dev.kigya.headway.home.api.model.out.HomeScreenNextInterviewTypeDto
import kotlin.random.Random

internal fun interface HomeScreenNextInterviewTypePicker {
    fun pick(): HomeScreenNextInterviewTypeDto
}

internal class RandomHomeScreenNextInterviewTypePicker(
    private val random: Random,
) : HomeScreenNextInterviewTypePicker {
    override fun pick(): HomeScreenNextInterviewTypeDto {
        val values = HomeScreenNextInterviewTypeDto.entries
        return values[random.nextInt(values.size)]
    }
}
