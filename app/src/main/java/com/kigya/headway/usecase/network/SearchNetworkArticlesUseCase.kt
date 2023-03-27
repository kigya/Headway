package com.kigya.headway.usecase.network

import com.kigya.headway.data.NewsRepository
import javax.inject.Inject

class SearchNetworkArticlesUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(searchQuery: String, pageNumber: Int) =
        newsRepository.searchForNews(searchQuery, pageNumber)
}