package dev.kigya.headway.core.session.android.google

import dev.kigya.headway.core.session.domain.contract.GoogleIdTokenAcquisitionContract
import org.koin.core.module.Module
import org.koin.dsl.module

fun headwayAndroidSessionGoogleModule(): Module = module {
    single { HeadwayAndroidGoogleSignInBridge() }
    single<GoogleIdTokenAcquisitionContract> {
        HeadwayAndroidGoogleIdTokenAcquisition(bridge = get())
    }
}
