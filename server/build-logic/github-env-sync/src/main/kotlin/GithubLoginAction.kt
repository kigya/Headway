import github.GithubApi
import gradle.GradleUserProperties
import util.GithubApiException
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import java.net.URI
import kotlin.math.min

internal class GithubLoginAction(
    private val clientId: String,
    private val tokenPropertyName: String,
    private val usernamePropertyName: String,
    private val autoOpenBrowser: Boolean,
    private val logger: (String) -> Unit,
) {

    fun runLogin(): String {
        val api = GithubApi(clientId = clientId)
        val deviceCode = api.requestDeviceCode()

        logger("GitHub authorization required.")
        logger("Open: ${deviceCode.verificationUri}")
        logger("Code: ${deviceCode.userCode}")

        val isSupported = !GraphicsEnvironment.isHeadless() && Desktop.isDesktopSupported()
        if (autoOpenBrowser && isSupported) {
            runCatching {
                val desktop = Desktop.getDesktop()
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(URI(deviceCode.verificationUri))
                } else {
                    logger("Browser opening is not supported. Open the URL manually: ${deviceCode.verificationUri}")
                }
            }.onFailure {
                logger("Could not open browser automatically. Open the URL manually: ${deviceCode.verificationUri}")
            }
        } else {
            logger("Open the URL manually: ${deviceCode.verificationUri}")
        }

        val deadlineMillis = System.currentTimeMillis() + deviceCode.expiresIn * 1000
        var intervalSeconds = deviceCode.interval.coerceAtLeast(1)
        var transientFailures = 0

        while (System.currentTimeMillis() < deadlineMillis) {
            Thread.sleep(intervalSeconds * 1000)

            val tokenResponse = try {
                api.pollAccessToken(deviceCode.deviceCode)
            } catch (e: GithubApiException) {
                if (e.retryable) {
                    transientFailures++
                    val backoff = min(30, intervalSeconds + transientFailures * 2)
                    logger("GitHub temporary error (${e.statusCode}). Retrying in ${backoff}s...")
                    intervalSeconds = backoff
                    continue
                } else {
                    throw e
                }
            }

            when (tokenResponse.error) {
                null -> {
                    val token = tokenResponse.accessToken
                        ?: error("GitHub returned success without access_token")

                    val user = api.getCurrentUser(token)

                    GradleUserProperties.write(tokenPropertyName, token)
                    GradleUserProperties.write(usernamePropertyName, user.login)

                    logger("Authorized as: ${user.login}")
                    logger("Saved token to ~/.gradle/gradle.properties")
                    return token
                }

                "authorization_pending" -> {
                    transientFailures = 0
                }

                "slow_down" -> {
                    intervalSeconds += 5
                    transientFailures = 0
                }

                "expired_token" -> {
                    error("Device code expired before authorization completed")
                }

                "access_denied" -> {
                    error("Authorization was denied by the user")
                }

                else -> {
                    error(
                        "GitHub authorization failed: ${tokenResponse.error}" +
                                (tokenResponse.errorDescription?.let { " - $it" } ?: "")
                    )
                }
            }
        }

        error("Timed out while waiting for GitHub authorization")
    }
}
