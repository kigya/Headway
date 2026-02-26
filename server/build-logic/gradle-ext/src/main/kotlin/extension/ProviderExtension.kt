@file:Suppress("NOTHING_TO_INLINE")

package extension

import org.gradle.api.provider.Provider

public inline fun Provider<String>.getInt(): Int = get().toInt()
