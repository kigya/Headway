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
import com.kigya.headway.R
import com.kigya.headway.data.dto.Article
import com.kigya.headway.databinding.ItemArticlePreviewBinding

typealias ArticleClickListener = (Article) -> Unit

class NewsAdapter(
    private val activityContext: Context,
) : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {

    private var onItemClickListener: ArticleClickListener? = null

    fun setOnItemClickListener(listener: ArticleClickListener) {
        onItemClickListener = listener
    }

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

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    inner class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val viewBinding by viewBinding(ItemArticlePreviewBinding::bind)

        fun bind(article: Article) {
            viewBinding.apply {
                Glide.with(root.context).load(article.urlToImage).into(ivArticleImage)
                tvSource.text = article.source?.name

                tvAuthor.text = activityContext.getString(
                    R.string.article_author,
                    article.author ?: activityContext.getString(R.string.anonymous_author),
                )

                tvTitle.text = article.title
                tvPublishedAt.text = article.publishedAt?.substring(0, 10) ?: String()
                root.setOnClickListener { onItemClickListener?.let { it(article) } }
            }
        }
    }
}
