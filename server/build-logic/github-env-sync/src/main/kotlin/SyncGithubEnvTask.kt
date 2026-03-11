import github.GithubApi
import gradle.GradleUserProperties
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import util.TemplateRenderer
import java.io.File

internal abstract class SyncGithubEnvTask : DefaultTask() {

    @get:Input
    abstract val owner: Property<String>

    @get:Input
    abstract val repo: Property<String>

    @get:Input
    abstract val environment: Property<String>

    @get:InputDirectory
    abstract val templatesDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Input
    abstract val tokenPropertyName: Property<String>

    @get:Input
    abstract val usernamePropertyName: Property<String>

    @get:Input
    abstract val failOnMissingVariables: Property<Boolean>

    @TaskAction
    fun sync() {
        GithubEnvSyncAction(
            owner = owner.get(),
            repo = repo.get(),
            environment = environment.get(),
            templatesDir = templatesDir.get().asFile,
            outputDir = outputDir.get().asFile,
            tokenPropertyName = tokenPropertyName.get(),
            usernamePropertyName = usernamePropertyName.get(),
            failOnMissingVariables = failOnMissingVariables.get(),
            logger = { logger.lifecycle(it) },
        ).runIfNeeded()
    }
}
