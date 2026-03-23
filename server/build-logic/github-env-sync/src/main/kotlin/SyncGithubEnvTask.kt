import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

internal abstract class SyncGithubEnvTask : DefaultTask() {

    @get:Input
    abstract val owner: Property<String>

    @get:Input
    abstract val repo: Property<String>

    @get:Input
    @get:Optional
    abstract val environment: Property<String>

    @get:Input
    abstract val environments: ListProperty<String>

    @get:InputDirectory
    abstract val templatesDir: DirectoryProperty

    @get:OutputDirectory
    abstract val generatedRootDir: DirectoryProperty

    @get:OutputDirectory
    @get:Optional
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
            environments = environments.orNull.orEmpty(),
            legacyEnvironment = environment.orNull,
            templatesDir = templatesDir.get().asFile,
            outputDir = outputDir.orNull?.asFile,
            generatedRootDir = generatedRootDir.get().asFile,
            tokenPropertyName = tokenPropertyName.get(),
            usernamePropertyName = usernamePropertyName.get(),
            failOnMissingVariables = failOnMissingVariables.get(),
            logger = { logger.lifecycle(it) },
        ).runIfNeeded()
    }
}
