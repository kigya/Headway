package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.Serializable

@Serializable
data class DatabasePreparationCatalogResponseDto(
    val items: List<DatabasePreparationCatalogItemDto>,
)

@Serializable
data class DatabasePreparationCatalogItemDto(
    val formatCode: DatabasePreparationFormatCode,
    val displayName: String,
    val shortDescription: String,
    val typeTags: List<String>,
    val defaultPreparationLanguage: String? = null,
    val selectablePreparationLanguages: List<String> = emptyList(),
)
