package com.kigya.headway.ui.news.detail

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.databinding.FragmentArticleBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.ui.news.NewsViewModel
import com.kigya.headway.utils.extensions.showToast

class ArticleDetailFragment : BaseFragment(R.layout.fragment_article) {

    override val viewModel: NewsViewModel by activityViewModels()
    private val viewBinding by viewBinding(FragmentArticleBinding::bind)
    private val args: ArticleDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewBinding) {
            setupWebView()
            setOnFabClickListener()
        }
    }

    private fun setOnFabClickListener() {
        viewBinding.fabSave.setOnClickListener { addToFavorites() }
    }

    private fun addToFavorites() {
        viewModel.saveArticle(args.article)
        showToast(getString(R.string.article_saved))
    }

    private fun FragmentArticleBinding.setupWebView() {
        webView.apply {
            webViewClient = WebViewClient()
            args.article.url?.let { loadUrl(it) }
        }
    }

    companion object {
        const val ARTICLE_TAG = "article"
    }
}
