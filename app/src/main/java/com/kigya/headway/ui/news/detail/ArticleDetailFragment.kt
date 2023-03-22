package com.kigya.headway.ui.news.detail

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.databinding.FragmentArticleBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ArticleDetailFragment : BaseFragment(R.layout.fragment_article) {

    override val viewModel: ArticleDetailViewModel by viewModels()
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
            args.article.url?.let { url -> loadUrl(url) }
        }
    }

    companion object {
        const val ARTICLE_TAG = "article"
    }
}
