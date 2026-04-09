package dev.kigya.headway

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import headway.app.headwaydesktop.generated.resources.Res
import headway.app.headwaydesktop.generated.resources.app_name
import headway.app.headwaydesktop.generated.resources.ic_headway_tray
import headway.app.headwaydesktop.generated.resources.tray_quit_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        width = defaultWindowWidth,
        height = defaultWindowHeight,
    )
    Tray(
        icon = painterResource(Res.drawable.ic_headway_tray),
        menu = {
            Item(
                text = stringResource(Res.string.tray_quit_app),
                onClick = ::exitApplication,
            )
        },
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = stringResource(Res.string.app_name),
    ) {
        val density = LocalDensity.current
        SideEffect {
            window.minimumSize = Dimension(
                with(density) { minimumWindowWidth.roundToPx() }.coerceAtLeast(1),
                with(density) { minimumWindowHeight.roundToPx() }.coerceAtLeast(1),
            )
        }
        App()
    }
}

private val defaultWindowWidth = 600.dp
private val defaultWindowHeight = 400.dp
private val minimumWindowWidth = 600.dp
private val minimumWindowHeight = 400.dp
