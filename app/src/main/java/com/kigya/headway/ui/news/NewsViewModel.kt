package com.kigya.headway.ui.news

import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.dto.Article
import com.kigya.headway.data.dto.NewsResponse
import com.kigya.headway.data.remote.NewsRepository
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.extensions.handleResource
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias NewsResourceFlow = MutableStateFlow<Resource<NewsResponse>>

@HiltViewModel
class NewsViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val newsRepository: NewsRepository,
) : BaseViewModel(dispatcher, logger) {

    private val _breakingNews: NewsResourceFlow =
        MutableStateFlow(Resource.Loading())
    val breakingNews = _breakingNews.asStateFlow()

    private var _breakingNewsPage = 1

    init {
        getBreakingNews("us")
    }

    private fun getBreakingNews(countryCode: String) {
        viewModelScope.launch(dispatcher) {
            _breakingNews.value = Resource.Loading()
            val response = newsRepository.getBreakingNews(countryCode, _breakingNewsPage)
            _breakingNews.value = response.handleResource()
        }
    }

    fun saveArticle(article: Article) {
        viewModelScope.launch(dispatcher) {
            newsRepository.upsertArticle(article)
        }
    }

    fun getSavedNewsFlow() = newsRepository.getSavedNews()
}
