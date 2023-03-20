package com.kigya.headway.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kigya.headway.data.dto.ArticleDto
import com.kigya.headway.data.model.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun upsertArticle(article: Article): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<Article>>
}
