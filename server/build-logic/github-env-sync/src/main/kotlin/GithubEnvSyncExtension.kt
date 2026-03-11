import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

abstract class GithubEnvSyncExtension {

    abstract val owner: Property<String>

    abstract val repo: Property<String>

    abstract val environment: Property<String>

    abstract val clientId: Property<String>

    abstract val tokenPropertyName: Property<String>

    abstract val usernamePropertyName: Property<String>

    abstract val templatesDir: DirectoryProperty

    abstract val outputDir: DirectoryProperty

    abstract val autoOpenBrowser: Property<Boolean>

    abstract val failOnMissingVariables: Property<Boolean>
}
