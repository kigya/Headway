package com.kigya.headway.data.dto

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int,
)
