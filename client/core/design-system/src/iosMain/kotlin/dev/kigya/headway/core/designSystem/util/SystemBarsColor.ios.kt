@file:Suppress("MagicNumber")

package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import kotlinx.cinterop.zeroValue
import platform.CoreGraphics.CGRect
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UINavigationBar
import platform.UIKit.UIUserInterfaceStyle
import platform.UIKit.UIView
import platform.UIKit.UIWindow
import platform.UIKit.setStatusBarStyle
import platform.UIKit.statusBarManager

@Composable
private fun rememberStatusBarView() = remember {
    val keyWindow: UIWindow? =
        UIApplication.sharedApplication.windows.firstOrNull { (it as? UIWindow)?.isKeyWindow() == true } as? UIWindow
    val tag = 3_848_245L

    keyWindow?.viewWithTag(tag) ?: run {
        val height =
            keyWindow?.windowScene?.statusBarManager?.statusBarFrame ?: zeroValue<CGRect>()
        val statusBarView = UIView(frame = height)
        statusBarView.tag = tag
        statusBarView.layer.zPosition = 999_999.0
        keyWindow?.addSubview(statusBarView)
        statusBarView
    }
}

@Composable
actual fun SystemBarsColor(color: SystemBarsColor) {
    val statusBar = rememberStatusBarView()

    fun setIosBarsColor(isLightContent: Boolean) {
        statusBar.backgroundColor = UIColor.clearColor

        UIApplication.sharedApplication.setStatusBarStyle(
            if (isLightContent) 1L else 3L,
        )

        UINavigationBar.appearance().apply {
            backgroundColor = UIColor.clearColor
            barTintColor = UIColor.clearColor
            tintColor = if (isLightContent) UIColor.whiteColor else UIColor.blackColor
            titleTextAttributes = mapOf(
                "NSForegroundColorAttributeName" to tintColor,
            )
        }
    }

    val keyWindow: UIWindow? =
        UIApplication.sharedApplication.windows
            .firstOrNull { (it as? UIWindow)?.isKeyWindow() == true }
            as? UIWindow
    val isDarkTheme = keyWindow?.traitCollection?.userInterfaceStyle ==
        UIUserInterfaceStyle.UIUserInterfaceStyleDark

    SideEffect {
        when (color) {
            SystemBarsColor.LIGHT -> setIosBarsColor(isLightContent = true)
            SystemBarsColor.DARK -> setIosBarsColor(isLightContent = false)
            SystemBarsColor.AUTO -> if (isDarkTheme) {
                setIosBarsColor(isLightContent = true)
            } else {
                setIosBarsColor(isLightContent = false)
            }
        }
    }
}
