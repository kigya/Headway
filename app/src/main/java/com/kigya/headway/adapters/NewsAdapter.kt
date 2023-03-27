package com.kigya.headway.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.kigya.headway.R
import com.kigya.headway.data.model.ArticleDomainModel
import com.kigya.headway.databinding.ItemArticlePreviewBinding

typealias ArticleClickListener = (ArticleDomainModel) -> Unit

class NewsAdapter(
    private val activityContext: Context,
    private val articleClickListener: ArticleClickListener,
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder =
        ArticleViewHolder(
            view = ItemArticlePreviewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ).root,
        )

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.bind(article)
    }

    override fun getItemCount(): Int = differ.currentList.size

    private val diffCallback = object : DiffUtil.ItemCallback<ArticleDomainModel>() {
        override fun areItemsTheSame(
            oldItem: ArticleDomainModel,
            newItem: ArticleDomainModel
        ): Boolean =
            oldItem.url == newItem.url

        override fun areContentsTheSame(
            oldItem: ArticleDomainModel,
            newItem: ArticleDomainModel
        ): Boolean =
            oldItem == newItem
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemArticlePreviewBinding::bind)

        fun bind(article: ArticleDomainModel) {
            viewBinding.apply {
                Glide.with(root.context)
                    .load(article.urlToImage)
                    .transform(CenterCrop(), RoundedCorners(ROUNDED_CORNERS_RADIUS))
                    .placeholder(R.drawable.image_loading_placeholder)
                    .into(ivArticleImage)

                tvSource.text = article.source?.name

                tvAuthor.text = activityContext.getString(
                    R.string.article_author,
                    article.author ?: activityContext.getString(R.string.anonymous_author),
                )

                tvTitle.text = article.title
                tvPublishedAt.text = article.publishedAt?.substring(0, 10) ?: String()
                root.setOnClickListener { articleClickListener(article) }
            }
        }
    }

    companion object {
        private const val ROUNDED_CORNERS_RADIUS = 10
    }
}
