package com.kigya.headway.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.kigya.headway.data.model.ArticleDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun upsertArticle(article: ArticleDomainModel): Long

    @Query("SELECT * FROM articles")
    fun getAllArticles(): Flow<List<ArticleDomainModel>>

    @Delete
    suspend fun deleteArticle(article: ArticleDomainModel)
}
