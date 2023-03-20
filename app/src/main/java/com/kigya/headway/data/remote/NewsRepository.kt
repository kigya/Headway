package com.kigya.headway.data.remote

import com.kigya.headway.data.dto.Article
import com.kigya.headway.data.local.db.ArticleDao
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsApi: NewsAPI,
    private val newsDao: ArticleDao,
) {

    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber)

    suspend fun upsertArticle(article: Article) = newsDao.upsertArticle(article)

    fun getSavedNews() = newsDao.getAllArticles()
}
