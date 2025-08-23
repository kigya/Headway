package dev.kigya.headway.core.designSystem.util

import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

@Composable
actual fun SystemBarsColor(color: SystemBarsColor) {
    val activity = LocalActivity.current as? ComponentActivity
    when (color) {
        SystemBarsColor.LIGHT -> activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )

        SystemBarsColor.DARK -> activity?.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
            ),
        )

        SystemBarsColor.AUTO -> activity?.enableEdgeToEdge()
    }
}
