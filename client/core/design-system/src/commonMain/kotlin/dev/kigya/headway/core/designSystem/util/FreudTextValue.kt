package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import dev.kigya.headway.core.designSystem.theme.FreudDsToken
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.jvm.JvmInline

sealed interface FreudTextFormatArg {

    companion object {

        fun string(value: String): FreudTextFormatArg = FreudTextFormatString(value)

        fun int(value: Int): FreudTextFormatArg = FreudTextFormatInt(value)

        fun long(value: Long): FreudTextFormatArg = FreudTextFormatLong(value)

        fun float(value: Float): FreudTextFormatArg = FreudTextFormatFloat(value)

        fun double(value: Double): FreudTextFormatArg = FreudTextFormatDouble(value)
    }
}

@JvmInline
internal value class FreudTextFormatString(val value: String) : FreudTextFormatArg

@JvmInline
internal value class FreudTextFormatInt(val value: Int) : FreudTextFormatArg

@JvmInline
internal value class FreudTextFormatLong(val value: Long) : FreudTextFormatArg

@JvmInline
internal value class FreudTextFormatFloat(val value: Float) : FreudTextFormatArg

@JvmInline
internal value class FreudTextFormatDouble(val value: Double) : FreudTextFormatArg

private fun FreudTextFormatArg.toPlatformFormatValue(): Any = when (this) {
    is FreudTextFormatString -> value
    is FreudTextFormatInt -> value
    is FreudTextFormatLong -> value
    is FreudTextFormatFloat -> value
    is FreudTextFormatDouble -> value
}

sealed interface FreudTextValue {
    val animation: FreudTextAnimation?
    val onFinishTrigger: FreudAnimationTrigger?

    @JvmInline
    value class PlainText internal constructor(
        internal val data: PlainTextData,
    ) : FreudTextValue {
        override val animation: FreudTextAnimation? get() = data.animation
        override val onFinishTrigger: FreudAnimationTrigger? get() = data.onFinishTrigger
        internal val source: FreudTextSource get() = data.source
    }

    @JvmInline
    value class RichText internal constructor(
        internal val data: RichTextData,
    ) : FreudTextValue {
        override val animation: FreudTextAnimation? get() = data.animation
        override val onFinishTrigger: FreudAnimationTrigger? get() = data.onFinishTrigger
        internal val content: FreudRichTextContent get() = data.content

        internal companion object {
            fun create(
                content: FreudRichTextContent,
                animation: FreudTextAnimation? = null,
                onFinishTrigger: FreudAnimationTrigger? = null,
            ): RichText = RichText(RichTextData(content, animation, onFinishTrigger))
        }
    }

    companion object {

        fun text(
            value: String,
            animation: FreudTextAnimation? = null,
            onFinishTrigger: FreudAnimationTrigger? = null,
        ): PlainText = PlainText(
            data = PlainTextData(
                source = FreudTextSource.Raw(value = value),
                animation = animation,
                onFinishTrigger = onFinishTrigger,
            ),
        )

        fun text(
            resource: StringResource,
            animation: FreudTextAnimation? = null,
            onFinishTrigger: FreudAnimationTrigger? = null,
            vararg formatArgs: FreudTextFormatArg,
        ): PlainText = PlainText(
            data = PlainTextData(
                source = FreudTextSource.Resource(
                    value = resource,
                    formatArgs = persistentListOf(*formatArgs),
                ),
                animation = animation,
                onFinishTrigger = onFinishTrigger,
            ),
        )

        fun rich(builder: FreudRichTextBuilder.() -> Unit): RichText = FreudRichTextBuilder()
            .apply(builder)
            .build()
    }
}

@Immutable
internal data class PlainTextData(
    val source: FreudTextSource,
    val animation: FreudTextAnimation? = null,
    val onFinishTrigger: FreudAnimationTrigger? = null,
)

@Immutable
internal data class RichTextData(
    val content: FreudRichTextContent,
    val animation: FreudTextAnimation? = null,
    val onFinishTrigger: FreudAnimationTrigger? = null,
)

class FreudRichTextBuilder internal constructor() {
    private val segments = mutableListOf<FreudRichTextSegment>()
    private var animation: FreudTextAnimation? = null
    private var onFinishTrigger: FreudAnimationTrigger? = null

    fun append(value: String) {
        segments += FreudRichTextSegment(
            source = FreudTextSource.Raw(value = value),
            color = null,
        )
    }

    fun append(
        resource: StringResource,
        vararg formatArgs: FreudTextFormatArg,
    ) {
        segments += FreudRichTextSegment(
            source = FreudTextSource.Resource(
                value = resource,
                formatArgs = persistentListOf(*formatArgs),
            ),
            color = null,
        )
    }

    fun colored(
        value: String,
        color: FreudDsToken<Color>,
    ) {
        segments += FreudRichTextSegment(
            source = FreudTextSource.Raw(value = value),
            color = color,
        )
    }

    fun colored(
        resource: StringResource,
        color: FreudDsToken<Color>,
        vararg formatArgs: FreudTextFormatArg,
    ) {
        segments += FreudRichTextSegment(
            source = FreudTextSource.Resource(
                value = resource,
                formatArgs = persistentListOf(*formatArgs),
            ),
            color = color,
        )
    }

    fun animate(
        animation: FreudTextAnimation,
        onFinish: FreudAnimationTrigger? = null,
    ) {
        this.animation = animation
        this.onFinishTrigger = onFinish
    }

    internal fun build(): FreudTextValue.RichText = FreudTextValue.RichText.create(
        content = FreudRichTextContent.Segments(value = segments.toPersistentList()),
        animation = animation,
        onFinishTrigger = onFinishTrigger,
    )
}

@Composable
internal fun FreudTextValue.resolveAnnotatedString(
    defaultContentColor: FreudDsToken<Color>,
): AnnotatedString = when (this) {
    is FreudTextValue.PlainText -> AnnotatedString(
        text = resolveTextSource(source = source),
    )

    is FreudTextValue.RichText -> {
        val segments = when (val richContent = content) {
            is FreudRichTextContent.Segments -> richContent.value
        }
        buildAnnotatedString {
            segments.forEach { segment ->
                val resolvedText = resolveTextSource(source = segment.source)
                val segmentColor = segment.color?.value ?: defaultContentColor.value
                withStyle(style = SpanStyle(color = segmentColor)) {
                    append(resolvedText)
                }
            }
        }
    }
}

@Composable
fun FreudTextValue.resolveToPlainString(): String = when (this) {
    is FreudTextValue.PlainText -> resolveTextSource(source = source)
    is FreudTextValue.RichText -> when (val c = content) {
        is FreudRichTextContent.Segments -> buildString {
            for (segment in c.value) {
                append(resolveTextSource(source = segment.source))
            }
        }
    }
}

@Composable
fun FreudTextValue?.resolveToPlainStringOrNull(): String? = this?.resolveToPlainString()

@Composable
internal fun resolveTextSource(source: FreudTextSource): String = when (source) {
    is FreudTextSource.Raw -> source.value
    is FreudTextSource.Resource -> freudStringResource(
        resource = source.value,
        formatArgs = source.formatArgs,
    )
}

@Composable
@Suppress("SpreadOperator")
private fun freudStringResource(
    resource: StringResource,
    formatArgs: ImmutableList<FreudTextFormatArg>,
): String {
    if (formatArgs.isEmpty()) {
        return stringResource(resource)
    }
    val platformArgs = Array(formatArgs.size) { index ->
        formatArgs[index].toPlatformFormatValue()
    }
    return stringResource(resource, *platformArgs)
}

internal sealed interface FreudTextSource {

    @JvmInline
    value class Raw(val value: String) : FreudTextSource

    @Immutable
    data class Resource(
        val value: StringResource,
        val formatArgs: ImmutableList<FreudTextFormatArg>,
    ) : FreudTextSource
}

internal sealed interface FreudRichTextContent {

    @JvmInline
    value class Segments(
        val value: ImmutableList<FreudRichTextSegment>,
    ) : FreudRichTextContent
}

internal data class FreudRichTextSegment(
    val source: FreudTextSource,
    val color: FreudDsToken<Color>?,
)
