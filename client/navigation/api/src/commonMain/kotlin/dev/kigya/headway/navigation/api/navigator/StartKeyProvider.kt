package dev.kigya.headway.navigation.api.navigator

import androidx.navigation3.runtime.NavKey

fun interface StartKeyProvider {
    fun start(): NavKey
}
