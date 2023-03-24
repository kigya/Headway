package com.kigya.headway.utils.extensions

import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.constants.API_SUBSCRIPTION_ERROR_CODE
import com.kigya.headway.utils.constants.INTERNET_CONNECTION_ERROR_CODE
import com.kigya.headway.utils.paging.PagingHelpersWrapper
import retrofit2.Response

fun Response<NewsResponseDomainModel>.handleResponse(pagingHelpersWrapper: PagingHelpersWrapper):
        Resource<NewsResponseDomainModel> {
    if (isSuccessful) {
        val body = body()
        if (body != null) {
            pagingHelpersWrapper.page++
            if (pagingHelpersWrapper.response == null) {
                pagingHelpersWrapper.response = body
            } else {
                val oldArticles = pagingHelpersWrapper.response?.articles
                val newArticles = body.articles
                oldArticles?.addAll(newArticles)
            }
            return Resource.Success(pagingHelpersWrapper.response ?: body)
        }
    } else {
        return when (code()) {
            INTERNET_CONNECTION_ERROR_CODE -> Resource.Error("No Internet Connection")
            API_SUBSCRIPTION_ERROR_CODE -> Resource.Error("API Subscription Expired")
            else -> Resource.Error("Error ${code()}")
        }
    }
    return Resource.Error(message())
}
