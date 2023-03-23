package com.kigya.headway.adapters.helpers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.attr
import com.kigya.headway.R

class HorizontalDelimiterDecoration(
    private val context: Context,
    private val dividerHeight: Float = DEFAULT_DIVIDER_HEIGHT,
    private val dividerPadding: Float = DEFAULT_DIVIDER_PADDING,
) : RecyclerView.ItemDecoration() {

    private val dividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        val attrs = intArrayOf(attr.colorOnPrimary)
        val typedArray = context.obtainStyledAttributes(attrs)
        val dividerColor = typedArray.getColor(0, 0)
        typedArray.recycle()

        dividerPaint.color = dividerColor
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + dividerPadding.toInt()
        val right = parent.width - parent.paddingRight - dividerPadding.toInt()
        val childCount = parent.childCount

        for (i in 0 until childCount - 1) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight.toInt()
            c.drawRect(
                left.toFloat(),
                top.toFloat(),
                right.toFloat(),
                bottom.toFloat(),
                dividerPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val dividerHeight = context.resources.getDimension(R.dimen.margin_m)
        outRect.bottom = dividerHeight.toInt()
    }

    companion object {
        private const val DEFAULT_DIVIDER_HEIGHT = 1f
        private const val DEFAULT_DIVIDER_PADDING = 16f
    }
}
