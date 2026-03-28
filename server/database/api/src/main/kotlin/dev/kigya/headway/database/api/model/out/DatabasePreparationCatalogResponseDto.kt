package dev.kigya.headway.database.api.model.out

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DatabasePreparationCatalogResponseDto(
    @SerialName("items")
    val items: List<DatabasePreparationCatalogItemDto>,
)

@Serializable
data class DatabasePreparationCatalogItemDto(
    @SerialName("formatCode")
    val formatCode: DatabasePreparationFormatCode,
    @SerialName("displayName")
    val displayName: String,
    @SerialName("shortDescription")
    val shortDescription: String,
    @SerialName("typeTags")
    val typeTags: List<String>,
    @SerialName("defaultPreparationLanguage")
    val defaultPreparationLanguage: String? = null,
    @SerialName("selectablePreparationLanguages")
    val selectablePreparationLanguages: List<String> = emptyList(),
)
