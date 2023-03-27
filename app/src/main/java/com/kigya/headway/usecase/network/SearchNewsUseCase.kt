package com.kigya.headway.usecase.network

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.ArticleDomainModel
import javax.inject.Inject

class SearchNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(searchQuery: String, pageNumber: Int) =
        newsRepository.searchForNews(searchQuery, pageNumber)
}