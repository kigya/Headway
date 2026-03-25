package dev.kigya.headway.home.internal.di

import dev.kigya.headway.home.internal.core.config.ConfigurationValues
import dev.kigya.headway.home.internal.domain.usecase.GetHomeScreenUseCase
import dev.kigya.headway.home.internal.domain.usecase.HomeScreenNextInterviewTypePicker
import dev.kigya.headway.home.internal.domain.usecase.HomeScreenSectionsBuilder
import dev.kigya.headway.home.internal.domain.usecase.RandomHomeScreenNextInterviewTypePicker
import org.koin.dsl.module
import java.time.Clock
import java.time.ZoneId
import kotlin.random.Random

internal val homeInternalModule = module {
    single<Clock> { Clock.system(ZoneId.of("CET")) }
    single<HomeScreenNextInterviewTypePicker> {
        RandomHomeScreenNextInterviewTypePicker(Random.Default)
    }
    single {
        HomeScreenSectionsBuilder(
            iconsPublicBaseUrl = ConfigurationValues.HOME_ICONS_PUBLIC_BASE_URL,
        )
    }
    single {
        GetHomeScreenUseCase(
            clock = get(),
            nextInterviewTypePicker = get(),
            sectionsBuilder = get(),
        )
    }
}
