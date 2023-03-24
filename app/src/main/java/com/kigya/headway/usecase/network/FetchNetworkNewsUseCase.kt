package com.kigya.headway.usecase.network

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.data.remote.NetworkConnectivityObserver
import com.kigya.headway.utils.constants.INTERNET_CONNECTION_ERROR_CODE
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import javax.inject.Inject

class FetchNetworkNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository,
    private val connectivityObserver: NetworkConnectivityObserver,
) {
    suspend operator fun invoke(
        countryCode: String,
        pageNumber: Int
    ): Response<NewsResponseDomainModel> =
        if (connectivityObserver.isInternetAvailable()) {
            newsRepository.getBreakingNews(countryCode, pageNumber)
        } else {
            Response.error(
                INTERNET_CONNECTION_ERROR_CODE,
                String().toResponseBody()
            )
        }
}
