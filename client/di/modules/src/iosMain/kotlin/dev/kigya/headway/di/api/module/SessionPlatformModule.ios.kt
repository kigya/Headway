package dev.kigya.headway.di.api.module

import dev.kigya.headway.core.secureStorage.IosSecureSessionStorage
import dev.kigya.headway.core.secureStorage.SecureSessionStorageContract
import dev.kigya.headway.core.session.domain.HeadwaySessionGatewayPlatform
import dev.kigya.headway.core.session.domain.SessionRuntimeIdentityContract
import dev.kigya.headway.di.api.DispatcherKey
import dev.kigya.headway.di.api.HeadwayGraphqlHttpUrl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import platform.Foundation.NSUUID
import platform.Foundation.NSUserDefaults

actual fun sessionPlatformModule(): Module = module {
    single { HeadwayGraphqlHttpUrl(DEV_GRAPHQL_URL) }
    single<SecureSessionStorageContract> {
        IosSecureSessionStorage(ioDispatcher = get(named(DispatcherKey.IO)))
    }
    single<SessionRuntimeIdentityContract> { IosSessionRuntimeIdentity() }
}

private class IosSessionRuntimeIdentity : SessionRuntimeIdentityContract {

    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults

    override val deviceFingerprint: String
        get() {
            defaults.stringForKey(FINGERPRINT_KEY)?.let { return it }
            val created = NSUUID().UUIDString
            defaults.setObject(created, FINGERPRINT_KEY)
            return created
        }

    override val sessionGatewayPlatform: HeadwaySessionGatewayPlatform = HeadwaySessionGatewayPlatform.Ios
}

private const val DEV_GRAPHQL_URL: String = "https://kigya-headway-dev-gateway.onrender.com/api/v1/graphql"

private const val FINGERPRINT_KEY: String = "headway_device_fingerprint"
