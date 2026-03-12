plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "dev.kigya.headway.gradle"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(libs.jackson.module.kotlin)
}

gradlePlugin {
    plugins {
        create("githubEnvSyncPlugin") {
            id = "dev.kigya.github-env-sync"
            implementationClass = "GithubEnvSyncPlugin"
            displayName = "GitHub Env Sync Plugin"
            description = "Authorize via GitHub, verify repo access, fetch Actions Variables, and generate env files"
        }
    }
}
