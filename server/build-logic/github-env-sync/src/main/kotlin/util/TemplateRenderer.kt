package util

internal object TemplateRenderer {

    private val pattern = Regex("""\$\{([A-Za-z_][A-Za-z0-9_]*)}""")

    fun render(
        template: String,
        values: Map<String, String>,
        failOnMissing: Boolean
    ): String {
        val missing = linkedSetOf<String>()

        val rendered = pattern.replace(template) { match ->
            val key = match.groupValues[1]
            val value = values[key]
            when {
                value != null -> EnvValueSanitizer.sanitize(value)
                failOnMissing -> {
                    missing += key
                    match.value
                }
                else -> ""
            }
        }

        if (missing.isNotEmpty()) {
            error("Missing variables for template rendering: ${missing.joinToString(", ")}")
        }

        return rendered
    }
}
