package dev.kigya.headway.core.session.domain.usecase

import com.sun.net.httpserver.HttpServer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DesktopOAuthLoopbackServer {

    private val lock = Any()

    @Volatile
    private var activeServer: HttpServer? = null

    private var shutdownDispatcher: CoroutineDispatcher? = null

    private var shutdownScope: CoroutineScope? = null

    private var scheduledShutdown: Job? = null

    fun configureShutdownDispatcher(dispatcher: CoroutineDispatcher) {
        synchronized(lock) {
            if (shutdownDispatcher === dispatcher) {
                return
            }
            shutdownDispatcher = dispatcher
            scheduledShutdown?.cancel()
            shutdownScope?.cancel()
            shutdownScope = CoroutineScope(SupervisorJob() + dispatcher)
        }
    }

    fun register(server: HttpServer) {
        synchronized(lock) {
            releaseLocked()
            activeServer = server
        }
    }

    fun release() {
        synchronized(lock) {
            scheduledShutdown?.cancel()
            scheduledShutdown = null
            releaseLocked()
        }
    }

    fun releaseIfActive(server: HttpServer) {
        synchronized(lock) {
            if (activeServer === server) {
                scheduledShutdown?.cancel()
                scheduledShutdown = null
                releaseLocked()
            }
        }
    }

    fun scheduleReleaseIfActive(
        server: HttpServer,
        delayMillis: Long,
    ) {
        synchronized(lock) {
            scheduledShutdown?.cancel()
            val scope = shutdownScope ?: return
            scheduledShutdown = scope.launch {
                delay(delayMillis)
                releaseIfActive(server)
            }
        }
    }

    private fun releaseLocked() {
        val server = activeServer
        activeServer = null
        runCatching { server?.stop(SERVER_STOP_DELAY_SECONDS) }
    }
}

private const val SERVER_STOP_DELAY_SECONDS: Int = 0
