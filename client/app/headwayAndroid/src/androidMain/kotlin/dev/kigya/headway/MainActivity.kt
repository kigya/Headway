package dev.kigya.headway

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dev.kigya.headway.core.session.android.google.HeadwayAndroidGoogleSignInBridge
import dev.kigya.headway.core.session.android.google.HeadwayAndroidGoogleSignInFlow
import org.koin.android.ext.android.inject

internal class MainActivity : ComponentActivity() {

    private val googleSignInBridge: HeadwayAndroidGoogleSignInBridge by inject()
    private val googleSignInFlow = HeadwayAndroidGoogleSignInFlow(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleSignInBridge.attach(googleSignInFlow)
        setContent { App() }
    }

    override fun onDestroy() {
        googleSignInFlow.onDestroy()
        googleSignInBridge.detach(googleSignInFlow)
        super.onDestroy()
    }
}
