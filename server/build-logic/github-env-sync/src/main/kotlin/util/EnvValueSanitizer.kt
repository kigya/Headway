package util

internal object EnvValueSanitizer {

    fun sanitize(value: String): String {
        require(!value.contains('\n') && !value.contains('\r')) {
            "Multiline values are not allowed in .env variables"
        }

        val escaped = value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("$", "\\$")
            .replace("`", "\\`")

        return "\"$escaped\""
    }
}
