package com.kigya.headway.ui.news.favorites

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.db.DeleteDatabaseArticleUseCase
import com.kigya.headway.usecase.db.FetchDatabaseArticlesUseCase
import com.kigya.headway.usecase.db.SaveDatabaseArticleUseCase
import com.kigya.headway.usecase.db.UpdateDatabaseArticlesUseCase
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val fetchDatabaseArticlesUseCase: FetchDatabaseArticlesUseCase,
    private val deleteDatabaseArticleUseCase: DeleteDatabaseArticleUseCase,
    private val saveDatabaseArticleUseCase: SaveDatabaseArticleUseCase,
    private val updateDatabaseArticlesUseCase: UpdateDatabaseArticlesUseCase,
) : BaseViewModel(dispatcher, logger) {
    fun getSavedNewsFlow() = fetchDatabaseArticlesUseCase()

    fun deleteArticle(article: ArticleDomainModel) {
        viewModelScope.launch(dispatcher) {
            deleteDatabaseArticleUseCase(article)
        }
    }

    fun saveArticle(article: ArticleDomainModel) {
        viewModelScope.launch(dispatcher) {
            saveDatabaseArticleUseCase(article)
        }
    }

    fun saveOrder(currentList: List<ArticleDomainModel>) {
        viewModelScope.launch(dispatcher) {
            updateDatabaseArticlesUseCase(currentList)
        }
    }
}
