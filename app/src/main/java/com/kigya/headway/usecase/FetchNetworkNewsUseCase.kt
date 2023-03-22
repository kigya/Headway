package com.kigya.headway.usecase

import com.kigya.headway.data.NewsRepository
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.data.remote.NetworkConnectivityObserver
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.nio.channels.NoConnectionPendingException
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
                400,
                String().toResponseBody()
            )
        }

}
