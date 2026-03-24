package dev.kigya.headway.gateway.graphql

internal enum class GatewayAppLocale {
    EN,
    RU,
}

internal fun parseGatewayAppLocale(
    xHeadwayLocale: String?,
    acceptLanguage: String?,
): GatewayAppLocale {
    when (xHeadwayLocale?.trim()?.lowercase()) {
        "ru" -> return GatewayAppLocale.RU
        "en" -> return GatewayAppLocale.EN
        else -> Unit
    }
    return parseAcceptLanguagePrimary(acceptLanguage) ?: GatewayAppLocale.EN
}

private fun parseAcceptLanguagePrimary(acceptLanguage: String?): GatewayAppLocale? {
    if (acceptLanguage.isNullOrBlank()) {
        return null
    }
    return acceptLanguage.split(',').asSequence()
        .map { part -> part.substringBefore(';').trim().lowercase() }
        .filter { tag -> tag.isNotEmpty() }
        .map { tag -> tag.substringBefore('-').trim() }
        .mapNotNull { primary ->
            when (primary) {
                "ru" -> GatewayAppLocale.RU
                "en" -> GatewayAppLocale.EN
                else -> null
            }
        }
        .firstOrNull()
}
