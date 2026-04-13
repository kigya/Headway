package dev.kigya.headway

import android.app.Application
import dev.kigya.headway.core.session.android.google.headwayAndroidSessionGoogleModule
import dev.kigya.headway.di.api.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class HeadwayApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@HeadwayApp)
            modules(listOf(headwayAndroidSessionGoogleModule()) + appModules)
        }
    }
}
