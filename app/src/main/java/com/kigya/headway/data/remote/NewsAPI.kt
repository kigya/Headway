package com.kigya.headway.data.remote

import com.kigya.headway.data.dto.NewsResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") countryCode: String = "us",
        @Query("page") page: Int = 1,
    ): Response<NewsResponseDto>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q") searchQuery: String,
        @Query("page") pageNumber: Int = 1,
        @Query("pageSize") pageSize: Int = 20,
    ): Response<NewsResponseDto>
}
