plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.github.env.sync)
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":gateway"))
    kover(project(":auth:internal"))
    kover(project(":database:internal"))
    kover(project(":home:internal"))
}

kover {
    reports {
        verify {
            rule("merged-auth-gateway-database-line-coverage") {
                minBound(22)
            }
        }
    }
}

tasks.register("verifyWithCoverage") {
    group = "verification"
    description = "Runs detekt and merged Kover line-coverage verification (auth, gateway, database/internal, home/internal)"
    dependsOn(tasks.named("detekt"), tasks.named("koverVerify"))
}

val gitHooksScript = file("../config/git/hooks/installer.gradle.kts")
if (gitHooksScript.exists()) {
    apply(from = gitHooksScript)
}

val templateScript = file("../config/templates/installer.gradle.kts")
if (templateScript.exists()) {
    apply(from = templateScript)
}

tasks.register("detekt") {
    group = "verification"
    description = "Runs detekt on all child modules"

    subprojects.forEach { subproject ->
        dependsOn(subproject.tasks.matching { it.name == "detekt" })
    }
}

githubEnvSync {
    owner.set("kigya")
    repo.set("Headway")

    templatesDir.set(layout.projectDirectory.dir("docker/template"))
    outputDir.set(layout.projectDirectory.dir("docker"))

    tokenPropertyName.set("github.env.sync.plugin.token")
    usernamePropertyName.set("github.env.sync.plugin.username")

    autoOpenBrowser.set(true)
    failOnMissingVariables.set(true)

    environments.set(listOf("dev", "prod"))
}
