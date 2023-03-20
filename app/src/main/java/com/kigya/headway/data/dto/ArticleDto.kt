package com.kigya.headway.data.dto

import android.os.Parcelable
import androidx.room.PrimaryKey
import com.kigya.headway.data.model.Article
import kotlinx.parcelize.Parcelize

data class ArticleDto(
    val author: String? = null,
    val content: String? = null,
    val description: String? = null,
    val publishedAt: String? = null,
    val source: SourceDto? = null,
    val title: String? = null,
    val url: String? = null,
    val urlToImage: String? = null,
)
