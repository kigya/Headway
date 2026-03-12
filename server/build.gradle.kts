plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.convention.github.env.sync)
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
    environment.set("dev")

    templatesDir.set(layout.projectDirectory.dir("docker/template"))
    outputDir.set(layout.projectDirectory.dir("docker"))

    tokenPropertyName.set("github.env.sync.token")
    usernamePropertyName.set("github.env.sync.username")

    autoOpenBrowser.set(true)
    failOnMissingVariables.set(true)
}
