package com.kigya.headway.ui.news.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.adapters.helpers.FadeInAnimator
import com.kigya.headway.adapters.helpers.HorizontalDelimiterDecoration
import com.kigya.headway.adapters.NewsAdapter
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.databinding.FragmentHomeBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.ui.news.detail.ArticleDetailFragment
import com.kigya.headway.ui.views.ResourceView
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.extensions.collectLatestLifecycleFlow
import com.kigya.headway.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)
    private var newsAdapter by Delegates.notNull<NewsAdapter>()
    private var pagingHandler by Delegates.notNull<PagingHandler>()
    private var resourceView by Delegates.notNull<ResourceView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupResourceView()
        setupRecyclerViewAdapter()
        collectFlow()
    }

    private fun setupResourceView() {
        resourceView = getActivityResourceView().apply {
            setTryAgainAction { viewModel.tryAgain() }
        }
    }

    private fun collectFlow() =
        collectLatestLifecycleFlow(viewModel.news) { response ->
            resourceView.apply {
                setResourceActions(
                    successBlock = { handleSuccessfulResponse(response) },
                )
                setResource(response)
            }
        }

    private fun getActivityResourceView(): ResourceView =
        requireActivity().findViewById(R.id.resourceView)

    private fun navigateToArticleDetailFragment(it: ArticleDomainModel) {
        val bundle = Bundle().apply { putSerializable(ArticleDetailFragment.ARTICLE_TAG, it) }
        findNavController().navigate(
            resId = R.id.action_breakingNewsFragment_to_articleFragment,
            args = bundle,
        )
    }

    private fun handleSuccessfulResponse(response: Resource<NewsResponseDomainModel>) {
        response.data?.let {
            newsAdapter.differ.submitList(it.articles.toList())
            val totalPages = it.totalResults / PagingHandler.HOME_NEWS_PAGE_SIZE + 2
            pagingHandler.isLastPage = viewModel.pagingHelpersWrapper.page == totalPages
        }
    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter(requireContext(), this::navigateToArticleDetailFragment)
        viewBinding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            pagingHandler = PagingHandler(viewBinding.rvBreakingNews) {
                viewModel.getBreakingNews(
                    "us"
                )
            }.apply {
                attach(resourceView)
            }
            addItemDecoration(HorizontalDelimiterDecoration(requireContext()))
            itemAnimator = FadeInAnimator()
        }
    }

}
