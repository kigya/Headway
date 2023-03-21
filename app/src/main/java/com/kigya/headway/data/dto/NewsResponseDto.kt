package com.kigya.headway.data.dto

data class NewsResponseDto(
    val articles: List<ArticleDto>,
    val status: String,
    val totalResults: Int,
)
