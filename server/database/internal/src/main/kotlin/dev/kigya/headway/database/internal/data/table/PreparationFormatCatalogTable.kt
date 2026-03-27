package dev.kigya.headway.database.internal.data.table

import org.jetbrains.exposed.v1.core.dao.id.UUIDTable

internal object PreparationFormatCatalogTable : UUIDTable(name = "public.preparation_format_catalog") {
    val formatCode = text(name = "format_code")
    val locale = text(name = "locale")
    val displayName = text(name = "display_name")
    val shortDescription = text(name = "short_description")
    val typeTagsJson = text(name = "type_tags")
    val defaultPreparationLanguage = text(name = "default_preparation_language").nullable()
    val customAllowedLanguagesJson = text(name = "custom_allowed_languages").nullable()

    init {
        uniqueIndex(formatCode, locale)
    }
}
