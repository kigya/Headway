package dev.kigya.headway.core.designSystem.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
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

    @JvmInline
    value class PlainText internal constructor(
        internal val source: FreudTextSource,
    ) : FreudTextValue

    @JvmInline
    value class RichText internal constructor(
        internal val content: FreudRichTextContent,
    ) : FreudTextValue {
        internal companion object {
            fun create(content: FreudRichTextContent): RichText = RichText(content)
        }
    }

    companion object {

        fun text(value: String): PlainText = PlainText(
            source = FreudTextSource.Raw(value = value),
        )

        fun text(
            resource: StringResource,
            vararg formatArgs: FreudTextFormatArg,
        ): PlainText = PlainText(
            source = FreudTextSource.Resource(
                value = resource,
                formatArgs = persistentListOf(*formatArgs),
            ),
        )

        fun rich(builder: FreudRichTextBuilder.() -> Unit): RichText = FreudRichTextBuilder()
            .apply(builder)
            .build()
    }
}

class FreudRichTextBuilder internal constructor() {
    private val segments = mutableListOf<FreudRichTextSegment>()

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

    internal fun build(): FreudTextValue.RichText = FreudTextValue.RichText.create(
        content = FreudRichTextContent.Segments(value = segments.toPersistentList()),
    )
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

    @Immutable
    data class Segments(
        val value: ImmutableList<FreudRichTextSegment>,
    ) : FreudRichTextContent
}

internal data class FreudRichTextSegment(
    val source: FreudTextSource,
    val color: FreudDsToken<Color>?,
)
