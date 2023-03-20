package com.kigya.headway.usecase

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.Article
import javax.inject.Inject

class SaveDatabaseNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(article: Article) =
        newsRepository.upsertArticle(article)
}
