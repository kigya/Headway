package com.kigya.headway.utils.mappers

import com.kigya.headway.data.dto.ArticleDto
import com.kigya.headway.data.dto.NewsResponseDto
import com.kigya.headway.data.dto.SourceDto
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.data.model.SourceDomainModel
import retrofit2.Response

fun ArticleDto.mapToArticle(): ArticleDomainModel =
    ArticleDomainModel(
        author = author,
        publishedAt = publishedAt,
        source = source?.toSourceDomainModel(),
        title = title,
        url = url,
        urlToImage = urlToImage,
    )

fun List<ArticleDto>.toMutableArticlesList(): MutableList<ArticleDomainModel> =
    map { it.mapToArticle() }.toMutableList()

fun SourceDto.toSourceDomainModel(): SourceDomainModel =
    SourceDomainModel(
        id = id,
        name = name,
    )

fun Response<NewsResponseDto>.toResponseDomainModel(): Response<NewsResponseDomainModel> =
    if (isSuccessful) {
        Response.success(
            NewsResponseDomainModel(
                articles = body()?.articles?.toMutableArticlesList() ?: mutableListOf(),
                status = body()?.status ?: "",
                totalResults = body()?.totalResults ?: 0,
            )
        )
    } else {
        Response.error(code(), errorBody() ?: throw Exception("Error body is null"))
    }