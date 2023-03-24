package com.kigya.headway.usecase.db

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.ArticleDomainModel
import javax.inject.Inject

class DeleteDatabaseNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(article: ArticleDomainModel) =
        newsRepository.deleteArticle(article)
}