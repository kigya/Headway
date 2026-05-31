package dev.kigya.headway.core.session.domain.usecase

import java.awt.AWTEvent
import java.awt.Frame
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.AWTEventListener
import java.awt.event.WindowEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DesktopOAuthAbandonmentMonitor {

    private val lock = Any()

    private var monitorScope: CoroutineScope? = null

    private var abandonCallback: (() -> Unit)? = null

    private var abandonJob: Job? = null

    @Volatile
    private var windowListenerRegistered: Boolean = false

    fun configureMonitorDispatcher(dispatcher: CoroutineDispatcher) {
        synchronized(lock) {
            monitorScope?.cancel()
            monitorScope = CoroutineScope(SupervisorJob() + dispatcher)
        }
    }

    fun registerWindowActivationListener() {
        if (windowListenerRegistered) {
            return
        }
        synchronized(lock) {
            if (windowListenerRegistered) {
                return
            }
            Toolkit.getDefaultToolkit().addAWTEventListener(
                DesktopOAuthWindowActivationListener,
                AWTEvent.WINDOW_EVENT_MASK,
            )
            windowListenerRegistered = true
        }
    }

    fun beginMonitoring(onAbandon: () -> Unit) {
        synchronized(lock) {
            abandonCallback = onAbandon
            abandonJob?.cancel()
            abandonJob = null
        }
    }

    fun endMonitoring() {
        synchronized(lock) {
            abandonCallback = null
            abandonJob?.cancel()
            abandonJob = null
        }
    }

    private fun onAppWindowActivated() {
        synchronized(lock) {
            val callback = abandonCallback ?: return
            val scope = monitorScope ?: return
            abandonJob?.cancel()
            abandonJob = scope.launch {
                delay(OAUTH_ABANDON_GRACE_MILLIS)
                val activeCallback = abandonCallback
                if (activeCallback === callback) {
                    activeCallback.invoke()
                }
            }
        }
    }

    private fun onAppWindowDeactivated() {
        synchronized(lock) {
            abandonJob?.cancel()
            abandonJob = null
        }
    }

    private object DesktopOAuthWindowActivationListener : AWTEventListener {

        override fun eventDispatched(event: AWTEvent) {
            val window = event.source as? Window ?: return
            if (!isAppWindow(window)) {
                return
            }
            when (event.id) {
                WindowEvent.WINDOW_ACTIVATED -> onAppWindowActivated()
                WindowEvent.WINDOW_DEACTIVATED -> onAppWindowDeactivated()
            }
        }
    }
}

private fun isAppWindow(window: Window): Boolean =
    window is Frame && window.isDisplayable && window.isVisible

private const val OAUTH_ABANDON_GRACE_MILLIS: Long = 1_500L
