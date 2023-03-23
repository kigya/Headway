package com.kigya.headway.utils.extensions

import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.utils.Resource
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
        return if (this.code() == 400) {
            Resource.Error("No Internet Connection")
        } else {
            Resource.Error("Error: ${this.code()}")
        }
    }
    return Resource.Error(message())
}
