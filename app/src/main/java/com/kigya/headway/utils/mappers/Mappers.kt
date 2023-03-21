package com.kigya.headway.utils.mappers

import com.kigya.headway.data.dto.ArticleDto
import com.kigya.headway.data.dto.NewsResponseDto
import com.kigya.headway.data.dto.SourceDto
import com.kigya.headway.data.model.Article
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.data.model.SourceDomainModel
import com.kigya.headway.utils.logger.LogCatLogger.log
import retrofit2.Response

fun ArticleDto.mapToArticle(): Article =
    Article(
        author = author,
        publishedAt = publishedAt,
        source = source?.toSourceDomainModel(),
        title = title,
        url = url,
        urlToImage = urlToImage,
    )

fun List<ArticleDto>.toListOfArticles(): List<Article> = map { it.mapToArticle() }

fun SourceDto.toSourceDomainModel(): SourceDomainModel =
    SourceDomainModel(
        id = id,
        name = name,
    )

fun Response<NewsResponseDto>.asResponseDomainModel(): Response<NewsResponseDomainModel> =
    if (isSuccessful) {
        Response.success(
            NewsResponseDomainModel(
                articles = body()?.articles?.toListOfArticles() ?: emptyList(),
                status = body()?.status ?: "",
                totalResults = body()?.totalResults ?: 0,
            )
        )
    } else {
        Response.error(code(), errorBody() ?: throw Exception("Error body is null"))
    }