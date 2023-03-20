package com.kigya.headway.ui.news.home

import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.FetchNetworkNewsUseCase
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.extensions.handleResource
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias NewsResourceFlow = MutableStateFlow<Resource<NewsResponseDomainModel>>

@HiltViewModel
class HomeViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val fetchNetworkNewsUseCase: FetchNetworkNewsUseCase,
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
            val response = fetchNetworkNewsUseCase(countryCode, _breakingNewsPage)
            _breakingNews.value = response.handleResource()
        }
    }

}
