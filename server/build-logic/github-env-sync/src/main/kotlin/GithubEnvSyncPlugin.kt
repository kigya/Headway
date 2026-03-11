import org.gradle.api.Plugin
import org.gradle.api.Project

internal class GithubEnvSyncPlugin : Plugin<Project> {

    override fun apply(target: Project) = target.applyGithubEnvSync()

    private fun Project.applyGithubEnvSync() {
        val ext = extensions.create(
            "githubEnvSync",
            GithubEnvSyncExtension::class.java
        )

        ext.tokenPropertyName.convention("github.env.sync.token")
        ext.usernamePropertyName.convention("github.env.sync.username")
        ext.autoOpenBrowser.convention(true)
        ext.failOnMissingVariables.convention(true)

        tasks.register("githubLogin", GithubLoginTask::class.java) {
            group = "github env sync"
            clientId.set(ext.clientId)
            tokenPropertyName.set(ext.tokenPropertyName)
            usernamePropertyName.set(ext.usernamePropertyName)
            autoOpenBrowser.set(ext.autoOpenBrowser)
        }

        tasks.register("syncGithubEnv", SyncGithubEnvTask::class.java) {
            group = "github env sync"
            owner.set(ext.owner)
            repo.set(ext.repo)
            environment.set(ext.environment)
            templatesDir.set(ext.templatesDir)
            outputDir.set(ext.outputDir)
            tokenPropertyName.set(ext.tokenPropertyName)
            usernamePropertyName.set(ext.usernamePropertyName)
            failOnMissingVariables.set(ext.failOnMissingVariables)
        }

        afterEvaluate {
            if (!isIdeSync(project)) return@afterEvaluate

            val logger = project.logger
                    logger.lifecycle("IDE Gradle sync detected")

            val outputDir = ext.outputDir.get().asFile
            val templatesDir = ext.templatesDir.get().asFile

            if (EnvSyncState.isUpToDate(
                    outputDir = outputDir,
                    templatesDir = templatesDir,
                    owner = ext.owner.get(),
                    repo = ext.repo.get(),
                    environment = ext.environment.get()
                )
            ) {
                logger.lifecycle("Env files already generated for IDE sync. Skipping githubLogin/syncGithubEnv.")
                return@afterEvaluate
            }

            try {
                GithubLoginAction(
                    clientId = ext.clientId.get(),
                    tokenPropertyName = ext.tokenPropertyName.get(),
                    usernamePropertyName = ext.usernamePropertyName.get(),
                    autoOpenBrowser = ext.autoOpenBrowser.get(),
                    logger = { logger.lifecycle(it) },
                ).runLogin()

                GithubEnvSyncAction(
                    owner = ext.owner.get(),
                    repo = ext.repo.get(),
                    environment = ext.environment.get(),
                    templatesDir = templatesDir,
                    outputDir = outputDir,
                    tokenPropertyName = ext.tokenPropertyName.get(),
                    usernamePropertyName = ext.usernamePropertyName.get(),
                    failOnMissingVariables = ext.failOnMissingVariables.get(),
                    logger = { logger.lifecycle(it) },
                ).runIfNeeded()
            } catch (t: Throwable) {
                logger.warn(
                    "githubEnvSync auto-run was skipped during IDE sync: ${t.message}. " +
                            "Project sync will continue. You can run githubLogin / syncGithubEnv manually."
                )
            }
        }
    }

    private fun isIdeSync(project: Project): Boolean {
        val ideaSync = project.providers.systemProperty("idea.sync.active").orNull == "true"
        val androidIde = project.providers.systemProperty("android.injected.invoked.from.ide").orNull == "true"
        return ideaSync || androidIde
    }
}
