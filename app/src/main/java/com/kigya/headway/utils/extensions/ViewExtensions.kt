package com.kigya.headway.utils.extensions

import android.view.View
import com.kigya.headway.ui.listeners.OnSwipeTouchListener

fun View.gone() = run { visibility = View.GONE }

fun View.visible() = run { visibility = View.VISIBLE }

fun View.invisible() = run { visibility = View.INVISIBLE }

infix fun View.visibleIf(condition: Boolean) =
    run { visibility = if (condition) View.VISIBLE else View.GONE }

infix fun View.goneIf(condition: Boolean) =
    run { visibility = if (condition) View.GONE else View.VISIBLE }

infix fun View.invisibleIf(condition: Boolean) =
    run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }

fun View.setOnSidesSwipeTouchListener(
    leftAction: (View) -> Unit = {},
    rightAction: (View) -> Unit = {},
) {
    setOnTouchListener(object : OnSwipeTouchListener(context) {
        override fun onSwipeLeft() {
            leftAction(this@setOnSidesSwipeTouchListener)
        }

        override fun onSwipeRight() {
            rightAction(this@setOnSidesSwipeTouchListener)
        }
    })
}