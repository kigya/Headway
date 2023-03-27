package com.kigya.headway.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kigya.headway.data.model.ArticleDomainModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Upsert
    suspend fun upsertArticle(article: ArticleDomainModel): Long

    @Delete
    suspend fun deleteArticle(article: ArticleDomainModel)

    @Query("UPDATE articles SET position = :newPosition WHERE id = :id")
    suspend fun updateArticlePosition(id: Int, newPosition: Int)

    @Transaction
    suspend fun updateArticlesPositions(articles: List<ArticleDomainModel>) {
        articles.forEachIndexed { index, article ->
            updateArticlePosition(article.id ?: index, index)
        }
    }

    suspend fun upsertArticleWithPosition(article: ArticleDomainModel) {
        val count = getArticlesCount()
        val articleWithPosition = article.copy(position = count)
        upsertArticle(articleWithPosition)
    }

    @Query("SELECT * FROM articles ORDER BY position ASC")
    fun getAllArticles(): Flow<List<ArticleDomainModel>>

    @Query("SELECT COUNT(*) FROM articles")
    fun getArticlesCount(): Int
}
