package dev.kigya.headway.di.api.module

import dev.kigya.headway.core.secureStorage.DesktopSecureSessionStorage
import dev.kigya.headway.core.secureStorage.SecureSessionStorageContract
import dev.kigya.headway.core.session.domain.HeadwaySessionGatewayPlatform
import dev.kigya.headway.core.session.domain.SessionRuntimeIdentityContract
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.core.session.domain.usecase.DesktopGoogleIdTokenAcquisition
import dev.kigya.headway.di.api.DispatcherKey
import dev.kigya.headway.di.api.HeadwayGraphqlHttpUrl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.notExists
import kotlin.random.Random

actual fun sessionPlatformModule(): Module = module {
    single { HeadwayGraphqlHttpUrl(DEV_GRAPHQL_URL) }
    single<SecureSessionStorageContract> {
        DesktopSecureSessionStorage(ioDispatcher = get(named(DispatcherKey.IO)))
    }
    single<SessionRuntimeIdentityContract> { DesktopSessionRuntimeIdentity() }
    single<GoogleIdTokenAcquisitionContract> {
        DesktopGoogleIdTokenAcquisition(
            ioDispatcher = get(named(DispatcherKey.IO)),
            googleOAuthClientSecret = "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",
        )
    }
}

private class DesktopSessionRuntimeIdentity : SessionRuntimeIdentityContract {

    private val path: Path =
        Path.of(System.getProperty("user.home"), ".headway", "device_fingerprint")

    override val deviceFingerprint: String
        get() {
            if (path.notExists()) {
                path.parent.createDirectories()
                val value = "${Random.nextLong()}-${System.nanoTime()}"
                Files.writeString(path, value)
                return value
            }
            return Files.readString(path)
        }

    override val sessionGatewayPlatform: HeadwaySessionGatewayPlatform = HeadwaySessionGatewayPlatform.Desktop
}

private const val DEV_GRAPHQL_URL: String = "https://kigya-headway-dev-gateway.onrender.com/api/v1/graphql"
