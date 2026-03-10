plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.detekt)

    implementation(projects.gradleExtension)
}
