package com.kigya.headway.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kigya.headway.data.model.ArticleDomainModel.Companion.TABLE_NAME
import java.io.Serializable

@Entity(
    tableName = TABLE_NAME,
)
data class ArticleDomainModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String? = null,
    val publishedAt: String? = null,
    val source: SourceDomainModel? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
    val position: Int = 0,
) : Serializable {
    companion object {
        const val TABLE_NAME = "articles"
        const val serialVersionUID = 1L
    }
}
