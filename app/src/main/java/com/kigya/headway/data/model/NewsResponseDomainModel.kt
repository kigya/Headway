package com.kigya.headway.data.model

data class NewsResponseDomainModel(
    val articles: MutableList<ArticleDomainModel>,
    val status: String,
    val totalResults: Int,
)