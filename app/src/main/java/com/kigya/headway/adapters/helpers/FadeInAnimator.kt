package com.kigya.headway.adapters.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class FadeInAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.animate()
            .alpha(1f)
            .setDuration(DEFAULT_DURATION)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    dispatchAnimationFinished(holder)
                }
            })
            .start()
        return false
    }

    companion object {
        private const val DEFAULT_DURATION = 250L
    }
}