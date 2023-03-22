package com.kigya.headway.data

import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.data.model.NewsResponseDomainModel
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface NewsRepository {
    suspend fun getBreakingNews(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDomainModel>

    suspend fun upsertArticle(article: ArticleDomainModel): Long
    fun getSavedNews(): Flow<List<ArticleDomainModel>>
}
