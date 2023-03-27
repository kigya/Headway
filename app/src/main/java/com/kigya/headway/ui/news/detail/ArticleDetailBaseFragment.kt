package com.kigya.headway.ui.news.detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebViewClient
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kigya.headway.R
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.databinding.FragmentArticleBinding
import com.kigya.headway.ui.base.BaseFragment
import com.kigya.headway.utils.extensions.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class ArticleDetailBaseFragment : BaseFragment(R.layout.fragment_article) {

    override val viewModel: ArticleDetailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentArticleBinding::bind)
    abstract val article: ArticleDomainModel
    private var isBackPressedCalled: Boolean = false

    /**
     * Used to change the state of current bottom menu item
     */
    @get:IdRes
    abstract val internalBottomMenuResource: Int

    /**
     *  Used to change the state of external bottom menu item
     */
    @get:IdRes
    abstract val externalBottomMenuResource: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleBackPressedState()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWebView()
        setOnFabClickListener()
        setActionBarTitle()
        setBottomNavigationItemChecked(internalBottomMenuResource)
        setBottomNavigationItemEnabled(internalBottomMenuResource, false)
        setOnRightSwipeListener()
    }

    private fun setOnRightSwipeListener() {
        viewBinding.flSwipeDetector.rightAction = {
            findNavController().navigateUp()
            isBackPressedCalled = true
        }
    }

    private fun handleBackPressedState() =
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isBackPressedCalled = true
                    findNavController().navigateUp()
                    setBottomNavigationItemChecked(internalBottomMenuResource)
                }
            })


    override fun onDestroy() {
        super.onDestroy()
        if (!isBackPressedCalled) {
            setBottomNavigationItemChecked(externalBottomMenuResource)
        }
        setBottomNavigationItemEnabled(internalBottomMenuResource, true)
    }

    private fun setBottomNavigationItemChecked(
        @IdRes targetResId: Int
    ) {
        getBottomNavigationViewItem(targetResId)?.isChecked = true
    }

    private fun setBottomNavigationItemEnabled(
        @IdRes targetResId: Int,
        enabled: Boolean
    ) {
        getBottomNavigationViewItem(targetResId)?.isEnabled = enabled
    }

    fun setFabActivated(isActivated: Boolean) {
        viewBinding.fabSave.isActivated = isActivated
    }

    private fun getBottomNavigationViewItem(
        @IdRes resId: Int
    ): MenuItem? {
        val bottomNavigationView =
            requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        return bottomNavigationView.menu.findItem(resId)
    }

    private fun setActionBarTitle() {
        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            article.source?.name
    }

    private fun setOnFabClickListener(fabAction: () -> Unit = {}) {
        with(viewBinding) {
            fabSave.setOnClickListener {
                if (fabSave.isActivated.not()) {
                    addToFavorites()
                    fabSave.isActivated = true
                } else {
                    removeFromFavorites()
                    fabSave.isActivated = false
                }
                fabAction()
            }
        }
    }

    private fun addToFavorites() {
        viewModel.saveArticle(article)
        showToast(getString(R.string.article_saved))
    }

    private fun removeFromFavorites() {
        viewModel.deleteArticle(article)
        showToast(getString(R.string.article_removed))
    }

    private fun setupWebView() {
        viewBinding.webView.apply {
            webViewClient = WebViewClient()
            article.url?.let { url -> loadUrl(url) }
        }
    }

    companion object {
        const val ARTICLE_TAG = "article"
    }
}
