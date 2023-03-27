package com.kigya.headway.ui.news.detail

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.kigya.headway.R
import com.kigya.headway.data.model.ArticleDomainModel

class FavoritesArticleDetailFragment : ArticleDetailBaseFragment() {

    private val args: FavoritesArticleDetailFragmentArgs by navArgs()

    override val article: ArticleDomainModel
        get() = args.article

    override val internalBottomMenuResource: Int
        get() = R.id.favoritesFragment
    override val externalBottomMenuResource: Int
        get() = R.id.homeFragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.setFabActivated(true)
    }
}