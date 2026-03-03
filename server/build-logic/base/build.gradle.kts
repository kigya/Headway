plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.gradle.detekt)

    implementation(projects.gradleExt)
}
