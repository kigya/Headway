package com.kigya.headway.ui.base

import androidx.lifecycle.ViewModel
import com.kigya.headway.di.IoDispatcher
import com.kigya.headway.utils.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
open class BaseViewModel @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val logger: Logger,
) : ViewModel()
