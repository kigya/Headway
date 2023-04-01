package com.kigya.headway.ui.news.home

import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.network.FetchNetworkArticlesUseCase
import com.kigya.headway.usecase.network.SearchNetworkArticlesUseCase
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.extensions.handleResponse
import com.kigya.headway.utils.logger.Logger
import com.kigya.headway.utils.paging.PagingHelpersWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

typealias NewsResourceFlow = MutableStateFlow<Resource<NewsResponseDomainModel>>

@HiltViewModel
class HomeViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val fetchNetworkArticlesUseCase: FetchNetworkArticlesUseCase,
    private val searchNetworkArticlesUseCase: SearchNetworkArticlesUseCase,
) : BaseViewModel(dispatcher, logger) {

    private val _news: NewsResourceFlow =
        MutableStateFlow(Resource.Loading())
    val news = _news.asStateFlow()

    var pagingHelpersWrapper by Delegates.notNull<PagingHelpersWrapper>()

    private var isSearchMode: Boolean = false

    init {
        initPagingWrapper()
        getBreakingNews("us")
    }

    private fun initPagingWrapper() {
        pagingHelpersWrapper = PagingHelpersWrapper(
            page = 1,
            response = null
        )
    }

    fun getBreakingNews(countryCode: String) {
        viewModelScope.launch(dispatcher) {
            _news.value = Resource.Loading()
            val response = fetchNetworkArticlesUseCase(countryCode, pagingHelpersWrapper.page)
            _news.value = response.handleResponse(pagingHelpersWrapper)
        }
    }

    fun searchForNews(searchQuery: String) {
        viewModelScope.launch(dispatcher) {
            _news.value = Resource.Loading()
            if (isSearchMode.not()) {
                pagingHelpersWrapper.response = null
                isSearchMode = true
            }
            val response = searchNetworkArticlesUseCase(searchQuery, pagingHelpersWrapper.page)
            _news.value = response.handleResponse(pagingHelpersWrapper)
        }
    }

    fun tryAgain() {
        getBreakingNews("us")
    }

}
