plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.example"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.18.2")
}

gradlePlugin {
    plugins {
        create("githubEnvSyncPlugin") {
            id = "dev.kygia.github-env-sync"
            implementationClass = "GithubEnvSyncPlugin"
            displayName = "GitHub Env Sync Plugin"
            description = "Authorize via GitHub, verify repo access, fetch Actions Variables, and generate env files"
        }
    }
}
