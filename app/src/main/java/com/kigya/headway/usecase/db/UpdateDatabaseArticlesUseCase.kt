package com.kigya.headway.usecase.db

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.ArticleDomainModel
import javax.inject.Inject

class UpdateDatabaseArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(article: List<ArticleDomainModel>) =
        newsRepository.updateArticlesPositions(article)
}