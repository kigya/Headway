package com.kigya.headway.ui.news.home

import android.widget.AbsListView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kigya.headway.ui.views.ResourceView

class PagingHandler(
    private val recyclerView: RecyclerView,
    private val shouldPaginateCallback: () -> Unit,
) {
    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= HOME_NEWS_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                shouldPaginateCallback()
                isScrolling = false
            }

            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            if (lastVisibleItemPosition == totalItemCount - 1) {
                resourceView?.height?.let { height -> recyclerView.setPadding(0, 0, 0, height) }
            } else {
                recyclerView.setPadding(0, 0, 0, defaultPadding)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private var resourceView: ResourceView? = null
    private var defaultPadding = 0

    fun attach(resourceView: ResourceView?, defaultPadding: Int = DEFAULT_PADDING) {
        this.resourceView = resourceView
        this.defaultPadding = defaultPadding
        recyclerView.addOnScrollListener(scrollListener)
    }

    companion object {
        const val HOME_NEWS_PAGE_SIZE = 20
        const val DEFAULT_PADDING = 200
    }
}