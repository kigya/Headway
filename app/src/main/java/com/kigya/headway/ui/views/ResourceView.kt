package com.kigya.headway.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import by.kirich1409.viewbindingdelegate.viewBinding
import com.kigya.headway.R
import com.kigya.headway.databinding.ItemResourceStateBinding
import com.kigya.headway.utils.Resource
import com.kigya.headway.utils.extensions.visible
import com.kigya.headway.utils.extensions.visibleIf

class ResourceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private val binding by viewBinding(ItemResourceStateBinding::bind)
    private var retryAction: (() -> Unit)? = null
    private var successBlock: () -> Unit = {}
    private var errorBlock: () -> Unit = {}

    init {
        inflate(context, R.layout.item_resource_state, this)
    }

    fun setTryAgainAction(action: () -> Unit) {
        this.retryAction = action
    }

    private fun setMessageText(message: String) {
        with(binding) {
            tvErrorMessage.apply {
                visible()
                text = message
            }
        }
    }

    fun <T> setResource(result: Resource<T>) {
        binding.tvErrorMessage visibleIf (result is Resource.Error<*>)
        binding.btnRetry visibleIf (result is Resource.Error<*>)
        binding.resourceProgressBar visibleIf (result is Resource.Loading<*>)
        if (result is Resource.Error<*>) {
            renderTryAgainButton()
            setMessageText(result.message ?: "Something went wrong")
            errorBlock()
        } else {
            successBlock()
        }
    }

    fun setResourceActions(
        successBlock: () -> Unit = {},
        errorBlock: () -> Unit = {}
    ) {
        this.successBlock = successBlock
        this.errorBlock = errorBlock
    }

    private fun renderTryAgainButton() =
        binding.btnRetry.setOnClickListener { retryAction?.invoke() }
}