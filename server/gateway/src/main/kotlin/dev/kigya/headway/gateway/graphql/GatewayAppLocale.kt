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

private data class WeightedGatewayLocale(
    val index: Int,
    val quality: Double,
    val locale: GatewayAppLocale,
)

private fun parseAcceptLanguagePrimary(acceptLanguage: String?): GatewayAppLocale? {
    if (acceptLanguage.isNullOrBlank()) {
        return null
    }
    return acceptLanguage.split(',')
        .asSequence()
        .mapIndexedNotNull { index, raw ->
            parseAcceptLanguagePart(part = raw.trim(), index = index)
        }
        .sortedWith(
            compareByDescending<WeightedGatewayLocale> { it.quality }
                .thenBy { it.index },
        )
        .firstOrNull()
        ?.locale
}

private fun parseAcceptLanguagePart(
    part: String,
    index: Int,
): WeightedGatewayLocale? {
    if (part.isEmpty()) {
        return null
    }
    val semicolonIndex = part.indexOf(';')
    val rangeWithoutParameters = if (semicolonIndex < 0) {
        part
    } else {
        part.substring(0, semicolonIndex)
    }
    val range = rangeWithoutParameters.trim().lowercase()
    if (range.isEmpty() || range == "*") {
        return null
    }
    val primarySubtag = range.substringBefore('-').trim()
    val locale = when (primarySubtag) {
        "ru" -> GatewayAppLocale.RU
        "en" -> GatewayAppLocale.EN
        else -> return null
    }
    val quality = if (semicolonIndex < 0) {
        1.0
    } else {
        parseAcceptLanguageQuality(parameters = part.substring(semicolonIndex + 1))
    }
    return WeightedGatewayLocale(index = index, quality = quality, locale = locale)
}

private fun parseAcceptLanguageQuality(parameters: String): Double {
    val qParameter = parameters.split(';')
        .asSequence()
        .map { segment -> segment.trim().lowercase() }
        .firstOrNull { segment -> segment.startsWith("q=") }
    return if (qParameter == null) {
        1.0
    } else {
        val value = qParameter.substring(2).trim().toDoubleOrNull()
        if (value != null && value in 0.0..1.0) {
            value
        } else {
            0.0
        }
    }
}
