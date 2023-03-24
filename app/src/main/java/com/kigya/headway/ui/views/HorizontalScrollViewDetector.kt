package com.kigya.headway.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class HorizontalScrollViewDetector(
    context: Context,
    attrs: AttributeSet?,
) : FrameLayout(context, attrs) {

    private var startX = 0f
    private var startY = 0f

    var leftAction: () -> Unit = {}

    var rightAction: () -> Unit = {}

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
            }

            MotionEvent.ACTION_MOVE -> {
                val endX = ev.x
                val endY = ev.y
                val deltaX = startX - endX
                val deltaY = startY - endY

                if (kotlin.math.abs(deltaX) - kotlin.math.abs(deltaY) > MIN_SCROLL_DISTANCE) {
                    if (deltaX > 0) {
                        leftAction()
                    } else {
                        rightAction()
                    }
                    parent.requestDisallowInterceptTouchEvent(true)
                    return true
                } else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            MotionEvent.ACTION_UP -> {
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }

        return super.onInterceptTouchEvent(ev)
    }

    companion object {
        const val MIN_SCROLL_DISTANCE = 150
    }
}