package com.kigya.headway.ui.news.home

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.adapters.NewsAdapter
import com.kigya.headway.adapters.helpers.FadeInAnimator
import com.kigya.headway.adapters.helpers.HorizontalDelimiterDecoration
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.data.model.NewsResponseDomainModel
import com.kigya.headway.databinding.FragmentHomeBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.ui.news.detail.ArticleDetailBaseFragment
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
    private var provider by Delegates.notNull<MenuProvider>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupResourceView()
        setupRecyclerViewAdapter()
        collectFlow()
        addActivityMenuProvider()
    }

    override fun onPause() {
        (requireActivity() as MenuHost).removeMenuProvider(provider)
        super.onPause()
    }

    private fun addActivityMenuProvider() {
        val menuHost = requireActivity() as? MenuHost
        provider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                inflater.inflate(R.menu.options_menu, menu)
                configureSearchItem(menu)
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean = true
        }

        menuHost?.addMenuProvider(provider)
    }

    private fun configureSearchItem(menu: Menu) {
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.searchForNews(query ?: String())
                pagingHandler = PagingHandler(viewBinding.rvBreakingNews) {
                    viewModel.searchForNews(query ?: String())
                }.apply {
                    attach(resourceView)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
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

    private fun navigateToArticleDetailFragment(domainModel: ArticleDomainModel) {
        val bundle = Bundle().apply {
            putSerializable(ArticleDetailBaseFragment.ARTICLE_TAG, domainModel)
        }
        findNavController().navigate(
            resId = R.id.action_homeFragment_to_homeArticleDetailFragment,
            args = bundle,
        )
    }

    private fun handleSuccessfulResponse(response: Resource<NewsResponseDomainModel>) {
        response.data?.let { domainModel ->
            newsAdapter.differ.submitList(domainModel.articles.toList())
            val totalPages = domainModel.totalResults / PagingHandler.HOME_NEWS_PAGE_SIZE + 2
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
