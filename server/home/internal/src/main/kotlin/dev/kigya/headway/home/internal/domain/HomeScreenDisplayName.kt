package dev.kigya.headway.home.internal.domain

internal fun resolveHomeDisplayName(
    userName: String,
    userEmail: String,
): String {
    val trimmedName = userName.trim()
    if (trimmedName.isNotEmpty()) {
        return trimmedName
    }
    val localPart = userEmail.substringBefore('@').trim()
    if (localPart.isNotEmpty()) {
        return localPart
    }
    return DEFAULT_HOME_DISPLAY_NAME
}

private const val DEFAULT_HOME_DISPLAY_NAME: String = "User"
