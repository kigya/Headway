package com.kigya.headway.utils.extensions

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
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

inline fun View.snack(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    action: Snackbar.() -> Unit
) {
    snack(resources.getString(messageRes), length, action)
}

inline fun View.snack(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    action: Snackbar.() -> Unit
) {
    val snack = Snackbar.make(this, message, length)
    snack.action()
    snack.show()
}