plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.serialization) apply false
}

val gitHooksScript = file("../config/git/hooks/installer.gradle.kts")
if (gitHooksScript.exists()) {
    apply(from = gitHooksScript)
}

val templateScript = file("../config/templates/installer.gradle.kts")
if (templateScript.exists()) {
    apply(from = templateScript)
}
