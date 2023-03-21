package com.kigya.headway.data.model

data class NewsResponseDomainModel(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int,
)