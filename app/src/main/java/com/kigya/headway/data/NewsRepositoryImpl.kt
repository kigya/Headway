package com.kigya.headway.data

import com.kigya.headway.data.local.db.ArticleDao
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.data.remote.NewsAPI
import com.kigya.headway.utils.mappers.toResponseDomainModel
import retrofit2.Response
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApi: NewsAPI,
    private val newsDao: ArticleDao,
) : NewsRepository {

    override suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        newsApi.getBreakingNews(countryCode, pageNumber).toResponseDomainModel()

    override suspend fun upsertArticle(article: ArticleDomainModel) =
        newsDao.upsertArticle(article)

    override fun getSavedNews() = newsDao.getAllArticles()

    override suspend fun deleteArticle(article: ArticleDomainModel) =
        newsDao.deleteArticle(article)

    override suspend fun searchForNews(
        searchQuery: String,
        pageNumber: Int
    ): Response<NewsResponseDomainModel> {
        return newsApi.searchForNews(searchQuery, pageNumber).toResponseDomainModel()
    }

}
