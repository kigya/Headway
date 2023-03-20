package com.kigya.headway.utils.api

import com.kigya.headway.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newUrl = originalRequest.url.newBuilder()
            .addQueryParameter(API_KEY_PARAM, BuildConfig.API_KEY)
            .build()
        val newRequest = originalRequest
            .newBuilder()
            .url(newUrl)
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        const val API_KEY_PARAM = "apiKey"
    }
}
