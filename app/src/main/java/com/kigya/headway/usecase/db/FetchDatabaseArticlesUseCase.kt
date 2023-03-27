package com.kigya.headway.usecase.db

import com.kigya.headway.data.NewsRepository
import javax.inject.Inject

class FetchDatabaseArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    operator fun invoke() = newsRepository.getSavedNews()
}
