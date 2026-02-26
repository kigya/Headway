plugins {
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.serialization) apply false
}

apply {
    from("../config/git/hooks/installer.gradle.kts")
    from("../config/templates/installer.gradle.kts")
}
