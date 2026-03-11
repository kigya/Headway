import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal abstract class GithubLoginTask : DefaultTask() {

    @get:Input
    abstract val clientId: Property<String>

    @get:Input
    abstract val tokenPropertyName: Property<String>

    @get:Input
    abstract val usernamePropertyName: Property<String>

    @get:Input
    abstract val autoOpenBrowser: Property<Boolean>

    @TaskAction
    fun login() {
        GithubLoginAction(
            clientId = clientId.get(),
            tokenPropertyName = tokenPropertyName.get(),
            usernamePropertyName = usernamePropertyName.get(),
            autoOpenBrowser = autoOpenBrowser.get(),
            logger = { logger.lifecycle(it) },
        ).runLogin()
    }
}
