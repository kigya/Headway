package dev.kigya.headway.core.apollo

import dev.kigya.headway.core.network.api.HeadwayAccessTokenStore
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
class HeadwayAccessTokenHolder : HeadwayAccessTokenStore {

    private val tokenRef = AtomicReference<String?>(null)

    override fun current(): String? = tokenRef.load()

    override fun update(value: String?) {
        tokenRef.store(value)
    }
}
