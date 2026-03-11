import github.GithubApi
import gradle.GradleUserProperties
import util.TemplateRenderer
import java.io.File

internal class GithubEnvSyncAction(
    private val owner: String,
    private val repo: String,
    private val environment: String,
    private val templatesDir: File,
    private val outputDir: File,
    private val tokenPropertyName: String,
    private val usernamePropertyName: String,
    private val failOnMissingVariables: Boolean,
    private val logger: (String) -> Unit,
) {

    fun runIfNeeded() {
        if (EnvSyncState.isUpToDate(outputDir, templatesDir, owner, repo, environment)) {
            logger("GitHub env files already generated and up-to-date. Skipping.")
            return
        }

        runSync()
    }

    private fun runSync() {
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
            username = currentUser.login
        )

        val allowed = permission.permission in setOf("read", "triage", "write", "maintain", "admin")
        if (!allowed) {
            error("User ${currentUser.login} does not have sufficient access to $owner/$repo")
        }

        val repoVars = api.getRepositoryVariables(token, owner, repo)
        val envVars = api.getEnvironmentVariables(token, owner, repo, environment)

        val merged = linkedMapOf<String, String>()
        merged.putAll(repoVars)
        merged.putAll(envVars)

        renderTemplates(templatesDir, outputDir, merged, failOnMissingVariables)

        EnvSyncState.markSuccess(
            outputDir = outputDir,
            templatesDir = templatesDir,
            owner = owner,
            repo = repo,
            environment = environment
        )

        logger("GitHub env sync completed.")
    }

    private fun renderTemplates(
        templatesDir: File,
        outputDir: File,
        values: Map<String, String>,
        failOnMissing: Boolean
    ) {
        require(templatesDir.exists()) { "Templates dir does not exist: ${templatesDir.absolutePath}" }

        if (!outputDir.exists()) {
            outputDir.mkdirs()
        } else {
            outputDir.listFiles()
                .forEach { file ->
                    if (file.isFile && file.startsWith("env.")) file.delete()
                }
        }

        val templates = templatesDir.listFiles()
            ?.filter { it.isFile && it.name.startsWith("env.") && it.name.endsWith(".template") }
            .orEmpty()

        if (templates.isEmpty()) {
            logger("No env.*.template files found in ${templatesDir.absolutePath}")
            return
        }

        templates.forEach { templateFile ->
            val rendered = TemplateRenderer.render(
                template = templateFile.readText(),
                values = values,
                failOnMissing = failOnMissing
            )

            val outputName = templateFile.name.removeSuffix(".template")

            val outputFile = File(outputDir, outputName)
            outputFile.writeText(rendered)

            logger("Generated ${outputFile.absolutePath}")
        }
    }
}
