package com.kigya.headway.ui.news.favorites

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kigya.headway.R
import com.kigya.headway.adapters.NewsAdapter
import com.kigya.headway.utils.extensions.onTouchResponseVibrate
import com.kigya.headway.utils.extensions.snack
import java.util.Collections

class ArticlesTouchHelper(
    private val context: Context,
    private val adapter: NewsAdapter,
    private val viewModel: FavoritesViewModel
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.LEFT
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.absoluteAdapterPosition
        val toPosition = target.absoluteAdapterPosition
        val list = adapter.differ.currentList.toMutableList()
        Collections.swap(list, fromPosition, toPosition)
        adapter.differ.submitList(list)
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = INTERMEDIATE_ALPHA
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = COMPLETE_ALPHA
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            ItemTouchHelper.LEFT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.onTouchResponseVibrate { leftAction(viewHolder) }
                } else {
                    leftAction(viewHolder)
                }
            }
        }
    }

    private fun leftAction(viewHolder: RecyclerView.ViewHolder) {
        val article =
            adapter.differ.currentList[viewHolder.absoluteAdapterPosition]
        viewModel.deleteArticle(article)
        viewHolder.itemView.snack(
            messageRes = R.string.article_removed,
            action = {
                setAction(context.getString(R.string.undo)) {
                    viewModel.saveArticle(article)
                }
            })
    }

    companion object {
        private const val INTERMEDIATE_ALPHA = 0.5f
        private const val COMPLETE_ALPHA = 1f
    }
}