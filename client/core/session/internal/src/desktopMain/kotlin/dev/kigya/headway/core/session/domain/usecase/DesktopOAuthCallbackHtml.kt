package dev.kigya.headway.core.session.domain.usecase

internal fun buildOAuthCallbackHtml(
    title: String,
    subtitle: String,
    buttonLabel: String,
): String = loadOAuthHtmlResource(OAUTH_CALLBACK_HTML_RESOURCE)
    .replace(HTML_PLACEHOLDER_TITLE, title)
    .replace(HTML_PLACEHOLDER_SUBTITLE, subtitle)
    .replace(HTML_PLACEHOLDER_BUTTON_LABEL, buttonLabel)
    .replace(HTML_PLACEHOLDER_HEADWAY_URI, HEADWAY_OAUTH_COMPLETE_URI)
    .replace(HTML_PLACEHOLDER_FALLBACK_URI, DesktopOAuthLoopbackEndpoint.openAppHttpUri)
    .replace(HTML_PLACEHOLDER_CLOSE_TAB_SCRIPT, loadCloseTabScript())

internal fun buildOAuthReturnedHtml(): String = loadOAuthHtmlResource(OAUTH_RETURNED_HTML_RESOURCE)
    .replace(HTML_PLACEHOLDER_CLOSE_TAB_SCRIPT, loadCloseTabScript())

private fun loadCloseTabScript(): String = loadOAuthHtmlResource(CLOSE_TAB_SCRIPT_RESOURCE)

private const val OAUTH_CALLBACK_HTML_RESOURCE: String =
    "/dev/kigya/headway/core/session/oauth/callback.html"

private const val OAUTH_RETURNED_HTML_RESOURCE: String =
    "/dev/kigya/headway/core/session/oauth/returned.html"

private const val CLOSE_TAB_SCRIPT_RESOURCE: String =
    "/dev/kigya/headway/core/session/oauth/close-tab.js"

private const val HTML_PLACEHOLDER_TITLE: String = "{{TITLE}}"

private const val HTML_PLACEHOLDER_SUBTITLE: String = "{{SUBTITLE}}"

private const val HTML_PLACEHOLDER_BUTTON_LABEL: String = "{{BUTTON_LABEL}}"

private const val HTML_PLACEHOLDER_HEADWAY_URI: String = "{{HEADWAY_URI}}"

private const val HTML_PLACEHOLDER_FALLBACK_URI: String = "{{FALLBACK_URI}}"

private const val HTML_PLACEHOLDER_CLOSE_TAB_SCRIPT: String = "{{CLOSE_TAB_SCRIPT}}"

private object DesktopOAuthCallbackHtmlResources

private fun loadOAuthHtmlResource(resourcePath: String): String {
    val stream = checkNotNull(
        DesktopOAuthCallbackHtmlResources::class.java.getResourceAsStream(resourcePath),
    ) {
        "Missing OAuth HTML resource: $resourcePath"
    }
    return stream.bufferedReader().use { reader -> reader.readText() }
}
