package com.kigya.headway.data

import com.kigya.headway.data.dto.ArticleDto
import com.kigya.headway.data.local.db.ArticleDao
import com.kigya.headway.data.model.Article
import com.kigya.headway.data.remote.NewsAPI
import com.kigya.headway.utils.mappers.asResponseDomainModel
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsAPI,
    private val newsDao: ArticleDao,
) : NewsRepository {

    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber).asResponseDomainModel()

    override suspend fun upsertArticle(article: Article) =
        newsDao.upsertArticle(article)

    override fun getSavedNews() = newsDao.getAllArticles()
}
