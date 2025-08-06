package dev.kigya.headway

import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import headway.app.headwaydesktop.generated.resources.Res
import headway.app.headwaydesktop.generated.resources.app_name
import headway.app.headwaydesktop.generated.resources.ic_headway_tray
import headway.app.headwaydesktop.generated.resources.tray_quit_app
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Tray(
        icon = painterResource(Res.drawable.ic_headway_tray),
        menu = {
            Item(
                text = stringResource(Res.string.tray_quit_app),
                onClick = ::exitApplication,
            )
        }
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
    ) {
        App()
    }
}
