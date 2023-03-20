package com.kigya.headway.ui.news.favorites

import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.ui.base.BaseViewModel
import com.kigya.headway.usecase.FetchDatabaseNewsUseCase
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    logger: Logger,
    private val fetchDatabaseNewsUseCase: FetchDatabaseNewsUseCase,
) : BaseViewModel(dispatcher, logger) {
    fun getSavedNewsFlow() = fetchDatabaseNewsUseCase()
}
