package com.kigya.headway.data

import com.kigya.headway.data.dto.ArticleDto
import com.kigya.headway.data.dto.NewsResponseDto
import com.kigya.headway.data.model.Article
import com.kigya.headway.data.model.NewsResponseDomainModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface NewsRepository {
    suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDomainModel>

    suspend fun upsertArticle(article: Article): Long
    fun getSavedNews(): Flow<List<Article>>
}
