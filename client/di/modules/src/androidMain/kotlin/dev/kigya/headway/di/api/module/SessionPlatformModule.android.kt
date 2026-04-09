package dev.kigya.headway.di.api.module

import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import dev.kigya.headway.core.secureStorage.AndroidSecureSessionStorage
import dev.kigya.headway.core.secureStorage.SecureSessionStorageContract
import dev.kigya.headway.core.session.domain.HeadwaySessionGatewayPlatform
import dev.kigya.headway.core.session.domain.SessionRuntimeIdentityContract
import dev.kigya.headway.di.api.DispatcherKey
import dev.kigya.headway.di.api.HeadwayGraphqlHttpUrl
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual fun sessionPlatformModule(): Module = module {
    single {
        val url = resolveGraphqlHttpUrl(androidContext())
        Log.i(HEADWAY_AUTH_LOG_TAG, "HeadwayGraphqlHttpUrl=$url")
        HeadwayGraphqlHttpUrl(value = url)
    }
    single(
        qualifier = named(HEADWAY_SECURE_SESSION_DATASTORE_SCOPE_NAME),
    ) {
        CoroutineScope(
            SupervisorJob() + get<CoroutineDispatcher>(named(DispatcherKey.IO)),
        )
    }.withOptions {
        onClose { scope: CoroutineScope? ->
            scope?.cancel()
        }
    }
    single<SecureSessionStorageContract> {
        AndroidSecureSessionStorage(
            context = androidContext(),
            ioDispatcher = get(named(DispatcherKey.IO)),
            dataStoreScope = get(named(HEADWAY_SECURE_SESSION_DATASTORE_SCOPE_NAME)),
        )
    }
    single<SessionRuntimeIdentityContract> { AndroidSessionRuntimeIdentity(androidContext()) }
}

private fun resolveGraphqlHttpUrl(context: Context): String {
    val applicationInfo = context.packageManager.getApplicationInfo(
        context.packageName,
        PackageManager.GET_META_DATA,
    )
    val fromManifest = applicationInfo.metaData?.getString(HEADWAY_GRAPHQL_HTTP_URL_META_KEY)?.trim()
    return if (fromManifest.isNullOrEmpty()) {
        DEFAULT_GRAPHQL_HTTP_URL
    } else {
        fromManifest
    }
}

private class AndroidSessionRuntimeIdentity(
    context: Context,
) : SessionRuntimeIdentityContract {

    private val applicationContext = context.applicationContext

    override val deviceFingerprint: String
        get() = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID,
        )
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: "android-unknown"

    override val sessionGatewayPlatform: HeadwaySessionGatewayPlatform = HeadwaySessionGatewayPlatform.Android
}

private const val HEADWAY_AUTH_LOG_TAG: String = "HeadwayAuth"

private const val HEADWAY_GRAPHQL_HTTP_URL_META_KEY: String = "headway.graphql.httpUrl"
private const val DEFAULT_GRAPHQL_HTTP_URL: String = "http://10.0.2.2:8080/api/v1/graphql"
private const val HEADWAY_SECURE_SESSION_DATASTORE_SCOPE_NAME: String =
    "HeadwaySecureSessionDataStoreScope"
