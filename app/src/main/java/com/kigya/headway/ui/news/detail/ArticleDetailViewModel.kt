package com.kigya.headway.ui.news.detail

import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.db.DeleteDatabaseArticleUseCase
import com.kigya.headway.usecase.db.SaveDatabaseArticleUseCase
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val saveDatabaseArticleUseCase: SaveDatabaseArticleUseCase,
    private val deleteDatabaseArticleUseCase: DeleteDatabaseArticleUseCase,
) : BaseViewModel(dispatcher, logger) {

    fun saveArticle(article: ArticleDomainModel) {
        viewModelScope.launch(dispatcher) {
            saveDatabaseArticleUseCase(article)
        }
    }

    fun deleteArticle(article: ArticleDomainModel) {
        viewModelScope.launch(dispatcher) {
            deleteDatabaseArticleUseCase(article)
        }
    }
}
