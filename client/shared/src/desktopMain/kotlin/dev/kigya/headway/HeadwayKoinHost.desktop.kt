package dev.kigya.headway

import androidx.compose.runtime.Composable
import dev.kigya.headway.di.api.appModules
import org.koin.compose.KoinApplication
import org.koin.dsl.KoinConfiguration

@Composable
actual fun HeadwayKoinHost(content: @Composable () -> Unit) {
    KoinApplication(
        configuration = KoinConfiguration { modules(appModules) },
        content = content,
    )
}
