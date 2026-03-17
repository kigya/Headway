import gradle.ProjectGradleProperties
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

abstract class GithubEnvSyncExtension @Inject constructor(
    objects: ObjectFactory,
) {

    abstract val owner: Property<String>

    abstract val repo: Property<String>

    abstract val environment: Property<String>

    abstract val environments: ListProperty<String>

    abstract val includeLocalEnvironment: Property<Boolean>

    val clientId: Property<String> = objects
        .property(String::class.java)
        .value(ProjectGradleProperties.read("github.app.clientId"))

    abstract val tokenPropertyName: Property<String>

    abstract val usernamePropertyName: Property<String>

    abstract val templatesDir: DirectoryProperty

    abstract val outputDir: DirectoryProperty

    abstract val generatedRootDir: DirectoryProperty

    abstract val autoOpenBrowser: Property<Boolean>

    abstract val failOnMissingVariables: Property<Boolean>

    abstract val runOnIdeSync: Property<Boolean>
}
