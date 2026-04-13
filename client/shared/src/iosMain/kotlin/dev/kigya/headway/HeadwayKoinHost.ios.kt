package dev.kigya.headway

import androidx.compose.runtime.Composable
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.di.api.appModules
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinConfiguration
import org.koin.dsl.module

private val headwayIosGoogleKoinModule = module {
    single<GoogleIdTokenAcquisitionContract> { IosGoogleIdTokenAcquisition() }
}

@Composable
actual fun HeadwayKoinHost(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = KoinConfiguration {
            modules(appModules + headwayIosGoogleKoinModule)
        },
        content = content,
    )
}
