package dev.kigya.headway.core.designSystem.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

object FreudIconDefaults {
    val defaultAlphaSpec: AnimationSpec<Float> = tween(
        durationMillis = DEFAULT_ANIM_DURATION_MS,
        easing = FastOutSlowInEasing,
    )

    val defaultScaleSpec: AnimationSpec<Float> = tween(
        durationMillis = DEFAULT_ANIM_DURATION_MS,
        easing = FastOutSlowInEasing,
    )

    fun fadeIn(
        alphaSpec: AnimationSpec<Float> = defaultAlphaSpec,
    ): FreudAnimatedIconAnimation = FreudAnimatedIconAnimation.FadeIn(alphaSpec)

    fun scale(
        scaleSpec: AnimationSpec<Float> = defaultScaleSpec,
        hiddenScale: Float = DEFAULT_HIDDEN_SCALE,
    ): FreudAnimatedIconAnimation = FreudAnimatedIconAnimation.Scale(scaleSpec, hiddenScale)

    fun fadeInScale(
        alphaSpec: AnimationSpec<Float> = defaultAlphaSpec,
        scaleSpec: AnimationSpec<Float> = defaultScaleSpec,
        hiddenScale: Float = DEFAULT_HIDDEN_SCALE,
    ): FreudAnimatedIconAnimation = FreudAnimatedIconAnimation.FadeInScale(alphaSpec, scaleSpec, hiddenScale)
}

@Composable
fun FreudIcon(
    resource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp>? = null,
    tint: FreudDsToken<Color>? = null,
) {
    val resolvedModifier = modifier.then(
        if (size != null) Modifier.size(size.value) else Modifier
    )

    Image(
        painter = painterResource(resource),
        contentDescription = contentDescription,
        modifier = resolvedModifier,
        colorFilter = tint?.let { ColorFilter.tint(it.value) },
    )
}

@Immutable
sealed interface FreudAnimatedIconAnimation {

    @Immutable
    data class FadeIn(
        val alphaSpec: AnimationSpec<Float> = FreudIconDefaults.defaultAlphaSpec,
    ) : FreudAnimatedIconAnimation

    @Immutable
    data class Scale(
        val scaleSpec: AnimationSpec<Float> = FreudIconDefaults.defaultScaleSpec,
        val hiddenScale: Float = DEFAULT_HIDDEN_SCALE,
    ) : FreudAnimatedIconAnimation

    @Immutable
    data class FadeInScale(
        val alphaSpec: AnimationSpec<Float> = FreudIconDefaults.defaultAlphaSpec,
        val scaleSpec: AnimationSpec<Float> = FreudIconDefaults.defaultScaleSpec,
        val hiddenScale: Float = DEFAULT_HIDDEN_SCALE,
    ) : FreudAnimatedIconAnimation
}

@Composable
fun FreudAnimatedIcon(
    resource: DrawableResource,
    contentDescription: String?,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp>? = null,
    tint: FreudDsToken<Color>? = null,
    animation: FreudAnimatedIconAnimation = FreudIconDefaults.fadeIn(),
) {
    val targetAlpha = if (isVisible) 1f else 0f

    when (animation) {
        is FreudAnimatedIconAnimation.FadeIn -> {
            val alpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = animation.alphaSpec,
                label = ANIM_ALPHA_LABEL,
            )

            FreudIcon(
                resource = resource,
                contentDescription = contentDescription,
                modifier = modifier.alpha(alpha),
                size = size,
                tint = tint,
            )
        }

        is FreudAnimatedIconAnimation.Scale -> {
            val scaleTarget = if (isVisible) 1f else animation.hiddenScale
            val scale by animateFloatAsState(
                targetValue = scaleTarget,
                animationSpec = animation.scaleSpec,
                label = ANIM_SCALE_LABEL,
            )

            FreudIcon(
                resource = resource,
                contentDescription = contentDescription,
                modifier = modifier
                    .scale(scale),
                size = size,
                tint = tint,
            )
        }

        is FreudAnimatedIconAnimation.FadeInScale -> {
            val alpha by animateFloatAsState(
                targetValue = targetAlpha,
                animationSpec = animation.alphaSpec,
                label = ANIM_ALPHA_LABEL,
            )

            val scaleTarget = if (isVisible) 1f else animation.hiddenScale
            val scale by animateFloatAsState(
                targetValue = scaleTarget,
                animationSpec = animation.scaleSpec,
                label = ANIM_SCALE_LABEL,
            )

            FreudIcon(
                resource = resource,
                contentDescription = contentDescription,
                modifier = modifier
                    .alpha(alpha)
                    .scale(scale),
                size = size,
                tint = tint,
            )
        }
    }
}

private const val ANIM_ALPHA_LABEL = "FreudAnimatedIcon.alpha"
private const val ANIM_SCALE_LABEL = "FreudAnimatedIcon.scale"

private const val DEFAULT_ANIM_DURATION_MS = 200
private const val DEFAULT_HIDDEN_SCALE = 0.92f
