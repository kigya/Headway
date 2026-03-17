import github.GithubApi
import gradle.GradleUserProperties
import util.EnvSyncState
import util.TemplateRenderer
import java.io.File

internal class GithubEnvSyncAction(
    private val owner: String,
    private val repo: String,
    private val environments: List<String>,
    private val legacyEnvironment: String?,
    private val includeLocalEnvironment: Boolean,
    private val templatesDir: File,
    private val outputDir: File?,
    private val generatedRootDir: File,
    private val tokenPropertyName: String,
    private val usernamePropertyName: String,
    private val failOnMissingVariables: Boolean,
    private val logger: (String) -> Unit,
) {

    fun runIfNeeded() {
        val targetEnvironments = resolveTargetEnvironments()
        val allUpToDate = targetEnvironments.all { environment ->
            EnvSyncState.isUpToDate(
                outputDir = resolveOutputDir(environment),
                templatesDir = templatesDir,
                owner = owner,
                repo = repo,
                environment = environment,
            )
        }

        if (allUpToDate) {
            logger("GitHub env files already generated and up-to-date. Skipping.")
            return
        }

        runSync()
    }

    fun runSync() {
        val token = GradleUserProperties.read(tokenPropertyName)
            ?: error("GitHub token not found. Run githubLogin first")

        val api = GithubApi()
        val currentUser = api.getCurrentUser(token)
        val username = GradleUserProperties.read(usernamePropertyName) ?: currentUser.login

        if (username != currentUser.login) {
            GradleUserProperties.write(usernamePropertyName, currentUser.login)
        }

        val permission = api.getCollaboratorPermission(
            token = token,
            owner = owner,
            repo = repo,
            username = currentUser.login,
        )

        val allowed = permission.permission in setOf("read", "triage", "write", "maintain", "admin")
        if (!allowed) {
            error("User ${currentUser.login} does not have sufficient access to $owner/$repo")
        }

        val repoVars = api.getRepositoryVariables(token, owner, repo)

        resolveTargetEnvironments().forEach { environment ->
            val merged = linkedMapOf<String, String>()
            merged.putAll(repoVars)
            if (environment != LOCAL_ENVIRONMENT) {
                val envVars = api.getEnvironmentVariables(token, owner, repo, environment)
                merged.putAll(envVars)
            }

            val environmentOutputDir = resolveOutputDir(environment)
            renderTemplates(templatesDir, environmentOutputDir, merged, failOnMissingVariables)

            EnvSyncState.markSuccess(
                outputDir = environmentOutputDir,
                templatesDir = templatesDir,
                owner = owner,
                repo = repo,
                environment = environment,
            )

            logger("GitHub env sync completed for '$environment'.")
        }
    }

    private fun resolveTargetEnvironments(): List<String> {
        val result = linkedSetOf<String>()
        result.addAll(environments.filter { it.isNotBlank() })
        if (result.isEmpty() && !legacyEnvironment.isNullOrBlank()) {
            result += legacyEnvironment
        }
        if (includeLocalEnvironment) {
            result += LOCAL_ENVIRONMENT
        }
        require(result.isNotEmpty()) {
            "No environments configured. Set githubEnvSync.environments or githubEnvSync.environment"
        }
        return result.toList()
    }

    private fun resolveOutputDir(environment: String): File {
        val legacyOutputDir = outputDir
        return if (legacyOutputDir != null && environment == legacyEnvironment && environments.isEmpty() && !includeLocalEnvironment) {
            legacyOutputDir
        } else {
            File(generatedRootDir, environment)
        }
    }

    private fun renderTemplates(
        templatesDir: File,
        outputDir: File,
        values: Map<String, String>,
        failOnMissing: Boolean,
    ) {
        require(templatesDir.exists()) { "Templates dir does not exist: ${templatesDir.absolutePath}" }

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }

        val templates = templatesDir.listFiles()
            ?.filter { it.isFile && it.name.endsWith(".template") }
            ?.sortedBy { it.name }
            .orEmpty()

        if (templates.isEmpty()) {
            logger("No *.template files found in ${templatesDir.absolutePath}")
            return
        }

        templates.forEach { templateFile ->
            val rendered = TemplateRenderer.render(
                template = templateFile.readText(),
                values = values,
                failOnMissing = failOnMissing,
            )

            val outputName = templateFile.name.removeSuffix(".template")
            val outputFile = File(outputDir, outputName)
            outputFile.writeText(rendered)
            logger("Generated ${outputFile.absolutePath}")
        }
    }

    private companion object {
        const val LOCAL_ENVIRONMENT = "local"
    }
}
