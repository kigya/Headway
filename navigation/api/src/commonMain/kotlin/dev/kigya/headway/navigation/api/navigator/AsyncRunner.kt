package dev.kigya.headway.navigation.api.navigator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@DslMarker
annotation class NavigatorDsl

@NavigatorDsl
interface NavigatorScope

fun interface AsyncRunner {
    context(_: NavigatorScope)
    operator fun invoke(block: suspend () -> Unit): Job
}

context(_: NavigatorScope)
fun CoroutineScope.asNavigationAsyncRunner(): AsyncRunner =
    AsyncRunner { block -> launch { block() } }
