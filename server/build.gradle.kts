plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
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
