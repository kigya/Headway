package com.kigya.headway.data.dto

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kigya.headway.data.dto.Article.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = TABLE_NAME,
)
@Parcelize
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: Source? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
) : Parcelable {
    companion object {
        const val TABLE_NAME = "articles"
    }
}
