package com.kigya.headway.utils.extensions

import com.kigya.headway.utils.Resource
import retrofit2.Response

fun <T> Response<T>.handleResource(): Resource<T> {
    if (isSuccessful) {
        val body = body()
        if (body != null) return Resource.Success(body)
    }
    return Resource.Error(message())
}
