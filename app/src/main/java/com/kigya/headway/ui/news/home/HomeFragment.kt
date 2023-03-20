package com.kigya.headway.ui.news.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.adapters.NewsAdapter
import com.kigya.headway.data.model.Article
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.databinding.FragmentHomeBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.ui.news.detail.ArticleDetailFragment
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewAdapter()
        collectFlow()
    }

    private fun collectFlow() =
        collectLatestLifecycleFlow(viewModel.breakingNews) { response ->
            when (response) {
                is Resource.Success -> handleSuccessfulResponse(response)
                is Resource.Error -> handleErrorResponse(response)
                is Resource.Loading -> handleLoading()
            }
        }

    private fun navigateToArticleDetailFragment(it: Article) {
        val bundle = Bundle().apply { putParcelable(ArticleDetailFragment.ARTICLE_TAG, it) }
        findNavController().navigate(
            resId = R.id.action_breakingNewsFragment_to_articleFragment,
            args = bundle,
        )
    }

    private fun handleSuccessfulResponse(response: Resource.Success<NewsResponseDomainModel>) {
        hideProgressBar()
        response.data?.let {
            newsAdapter.differ.submitList(it.articles)
        }
    }

    private fun handleErrorResponse(response: Resource.Error<*>) {
        hideProgressBar()
        response.message?.let {
            showToast(it)
        }
    }

    private fun handleLoading() {
        showProgressBar()
    }

    private fun hideProgressBar() {
        viewBinding.progressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        viewBinding.progressBar.visibility = View.VISIBLE
    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter(requireContext(), this::navigateToArticleDetailFragment)
        viewBinding.rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
