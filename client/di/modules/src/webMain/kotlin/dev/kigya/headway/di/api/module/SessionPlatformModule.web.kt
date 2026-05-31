package dev.kigya.headway.di.api.module

import dev.kigya.headway.core.secureStorage.SecureSessionStorageContract
import dev.kigya.headway.core.secureStorage.WebSecureSessionStorage
import dev.kigya.headway.core.session.domain.HeadwaySessionGatewayPlatform
import dev.kigya.headway.core.session.domain.SessionRuntimeIdentityContract
import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import dev.kigya.headway.di.api.DispatcherKey
import dev.kigya.headway.di.api.HeadwayGraphqlHttpUrl
import kotlinx.browser.localStorage
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.random.Random

actual fun sessionPlatformModule(): Module = module {
    single { HeadwayGraphqlHttpUrl(DEV_GRAPHQL_URL) }
    single<SecureSessionStorageContract> {
        WebSecureSessionStorage(ioDispatcher = get(named(DispatcherKey.IO)))
    }
    single<SessionRuntimeIdentityContract> { WebSessionRuntimeIdentity() }
    single<GoogleIdTokenAcquisitionContract> { WasmGoogleIdTokenAcquisition() }
}

private class WebSessionRuntimeIdentity : SessionRuntimeIdentityContract {

    override val deviceFingerprint: String
        get() {
            localStorage.getItem(FINGERPRINT_KEY)?.let { return it }
            val created = "${Random.nextLong()}-${Random.nextLong()}"
            localStorage.setItem(FINGERPRINT_KEY, created)
            return created
        }

    override val sessionGatewayPlatform: HeadwaySessionGatewayPlatform = HeadwaySessionGatewayPlatform.Web
}

private const val DEV_GRAPHQL_URL: String = "http://localhost:8080/api/v1/graphql"
private const val FINGERPRINT_KEY: String = "headway_device_fingerprint"
