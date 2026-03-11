import gradle.GradleUserProperties
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

internal abstract class GithubLogoutTask : DefaultTask() {

    @get:Input
    abstract val tokenPropertyName: Property<String>

    @get:Input
    abstract val usernamePropertyName: Property<String>

    @TaskAction
    fun logout() {
        GradleUserProperties.remove(tokenPropertyName.get())
        GradleUserProperties.remove(usernamePropertyName.get())

        logger.lifecycle("Removed GitHub auth data from ~/.gradle/gradle.properties")
    }
}
