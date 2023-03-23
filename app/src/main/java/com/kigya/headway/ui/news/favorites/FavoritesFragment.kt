package com.kigya.headway.ui.news.favorites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.adapters.NewsAdapter
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.databinding.FragmentFavoritesBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.ui.news.detail.ArticleDetailFragment
import com.kigya.headway.utils.extensions.collectLatestLifecycleFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlin.properties.Delegates

@AndroidEntryPoint
class FavoritesFragment : BaseFragment(R.layout.fragment_favorites) {

    override val viewModel: FavoritesViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentFavoritesBinding::bind)
    private var newsAdapter by Delegates.notNull<NewsAdapter>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViewAdapter()
        collectNewsFlow()
    }

    private fun collectNewsFlow() =
        collectLatestLifecycleFlow(viewModel.getSavedNewsFlow()) { articles ->
            newsAdapter.differ.submitList(articles)
        }

    private fun navigateToArticleDetailFragment(domainModel: ArticleDomainModel) {
        val bundle = Bundle().apply {
            putSerializable(ArticleDetailFragment.ARTICLE_TAG, domainModel)
        }
        findNavController().navigate(
            R.id.action_savedNewsFragment_to_articleFragment,
            bundle,
        )
    }

    private fun setupRecyclerViewAdapter() {
        newsAdapter = NewsAdapter(requireContext(), this::navigateToArticleDetailFragment)
        viewBinding.rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
