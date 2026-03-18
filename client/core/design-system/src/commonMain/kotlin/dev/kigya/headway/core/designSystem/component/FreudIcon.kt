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
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

object FreudIconDefaults {
    private const val DEFAULT_ANIM_DURATION_MS = 200
    private const val DEFAULT_HIDDEN_SCALE = 0.92f

    val defaultFadeInScaleAlphaSpec: AnimationSpec<Float> = tween(
        durationMillis = DEFAULT_ANIM_DURATION_MS,
        easing = FastOutSlowInEasing,
    )

    val defaultFadeInScaleScaleSpec: AnimationSpec<Float> = tween(
        durationMillis = DEFAULT_ANIM_DURATION_MS,
        easing = FastOutSlowInEasing,
    )

    fun fadeInScale(
        alphaSpec: AnimationSpec<Float> = defaultFadeInScaleAlphaSpec,
        scaleSpec: AnimationSpec<Float> = defaultFadeInScaleScaleSpec,
        hiddenScale: Float = DEFAULT_HIDDEN_SCALE,
    ): FreudAnimatedIconAnimation = FreudAnimatedIconAnimation.FadeInScale(
        alphaSpec = alphaSpec,
        scaleSpec = scaleSpec,
        hiddenScale = hiddenScale,
    )
}

@Composable
fun FreudIcon(
    resource: DrawableResource,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp>? = null,
    tint: FreudDsToken<Color>? = null,
) {
    FreudIconContent(
        size = size,
        modifier = modifier,
        resource = resource,
        contentDescription = contentDescription,
        tint = tint,
    )
}

@Composable
fun FreudIcon(
    resource: DrawableResource,
    contentDescription: StringResource,
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp>? = null,
    tint: FreudDsToken<Color>? = null,
) {
    FreudIconContent(
        size = size,
        modifier = modifier,
        resource = resource,
        contentDescription = stringResource(contentDescription),
        tint = tint,
    )
}

@Composable
private fun FreudIconContent(
    size: FreudDsToken<Dp>?,
    resource: DrawableResource,
    contentDescription: String?,
    tint: FreudDsToken<Color>?,
    modifier: Modifier = Modifier,
) {
    val resolvedModifier = if (size != null) modifier.then(Modifier.size(size.value)) else modifier

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
    data class FadeInScale(
        val alphaSpec: AnimationSpec<Float> = FreudIconDefaults.defaultFadeInScaleAlphaSpec,
        val scaleSpec: AnimationSpec<Float> = FreudIconDefaults.defaultFadeInScaleScaleSpec,
        val hiddenScale: Float = 0.92f,
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
    animation: FreudAnimatedIconAnimation = FreudIconDefaults.fadeInScale(),
) {
    FreudAnimatedIconContent(
        animation = animation,
        isVisible = isVisible,
        resource = resource,
        contentDescription = contentDescription,
        modifier = modifier,
        size = size,
        tint = tint,
    )
}

@Composable
fun FreudAnimatedIcon(
    resource: DrawableResource,
    contentDescription: StringResource,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
    size: FreudDsToken<Dp>? = null,
    tint: FreudDsToken<Color>? = null,
    animation: FreudAnimatedIconAnimation = FreudIconDefaults.fadeInScale(),
) {
    FreudAnimatedIconContent(
        animation = animation,
        isVisible = isVisible,
        resource = resource,
        contentDescription = stringResource(contentDescription),
        modifier = modifier,
        size = size,
        tint = tint,
    )
}

@Composable
private fun FreudAnimatedIconContent(
    animation: FreudAnimatedIconAnimation,
    isVisible: Boolean,
    resource: DrawableResource,
    contentDescription: String?,
    size: FreudDsToken<Dp>?,
    tint: FreudDsToken<Color>?,
    modifier: Modifier = Modifier,
) {
    val anim = animation as FreudAnimatedIconAnimation.FadeInScale

    val targetAlpha = if (isVisible) 1f else 0f
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = anim.alphaSpec,
        label = ANIM_ALPHA_LABEL,
    )

    val targetScale = if (isVisible) 1f else anim.hiddenScale
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = anim.scaleSpec,
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

private const val ANIM_ALPHA_LABEL = "FreudAnimatedIcon.alpha"
private const val ANIM_SCALE_LABEL = "FreudAnimatedIcon.scale"
