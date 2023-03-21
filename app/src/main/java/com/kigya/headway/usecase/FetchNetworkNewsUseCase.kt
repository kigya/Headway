package com.kigya.headway.usecase

import com.kigya.headway.data.NewsRepository
import javax.inject.Inject

class FetchNetworkNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
) {
    suspend operator fun invoke(countryCode: String, pageNumber: Int) =
        newsRepository.getBreakingNews(countryCode, pageNumber)
}
