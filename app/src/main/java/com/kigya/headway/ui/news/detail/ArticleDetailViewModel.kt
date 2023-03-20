package com.kigya.headway.ui.news.detail

import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.model.Article
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.SaveDatabaseNewsUseCase
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val saveDatabaseNewsUseCase: SaveDatabaseNewsUseCase,
) : BaseViewModel(dispatcher, logger) {

    fun saveArticle(article: Article) {
        viewModelScope.launch(dispatcher) {
            saveDatabaseNewsUseCase(article)
        }
    }
}