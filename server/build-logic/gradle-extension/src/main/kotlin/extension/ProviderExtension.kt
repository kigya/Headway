package extension

import org.gradle.api.provider.Provider

public fun Provider<String>.getInt(): Int = get().toInt()
