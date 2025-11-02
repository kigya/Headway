package dev.kigya.headway.di.api.module

import dev.kigya.headway.di.api.DispatcherKey
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainCoroutineDispatcher
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal expect val ioDispatcher: CoroutineDispatcher

val dispatcherModule = module {
    single<CoroutineDispatcher>(
        qualifier = named(DispatcherKey.IO),
    ) { ioDispatcher }

    single<MainCoroutineDispatcher>(
        qualifier = named(DispatcherKey.Main),
    ) { Dispatchers.Main }

    single<CoroutineDispatcher>(
        qualifier = named(DispatcherKey.Default),
    ) { Dispatchers.Default }
}
