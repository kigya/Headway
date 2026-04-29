import base.configureDesktopApplication
import extension.desktopMainDependencies
import java.io.File
import org.gradle.api.tasks.JavaExec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.convention.base.desktopApplication)
    alias(libs.plugins.convention.component.compose)
}

configureDesktopApplication {
    mainClass.set("dev.kigya.headway.MainKt")
    packageName.set("Headway")
    packageVersion.set("1.0.0")
    iconDir.set(file("src/desktopMain/composeResources/drawable"))
    iconBaseName.set("ic_headway")
    dockName.set("Headway")
    formats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
}

desktopMainDependencies {
    projects {
        implementation(shared)
    }
}

private val headwayDesktopJavaExecEnvTaskNames: Set<String> =
    setOf(
        "run",
        "desktopRun",
        "runRelease",
        "runDistributable",
        "runReleaseDistributable",
        "hotRunDesktop",
        "hotRunDesktopAsync",
        "hotDevDesktop",
        "hotDevDesktopAsync",
    )

private val headwayDesktopEnvFileForJavaExec: File =
    providers.gradleProperty("headwayDesktopEnv").orElse("dev").get().let { envDirectoryName ->
        rootProject.layout.projectDirectory
            .file("secrets/$envDirectoryName/env.desktop")
            .asFile
    }

tasks.withType<JavaExec>().configureEach {
    if (name !in headwayDesktopJavaExecEnvTaskNames) {
        return@configureEach
    }
    doFirst {
        val envFile = headwayDesktopEnvFileForJavaExec
        if (!envFile.exists()) {
            return@doFirst
        }
        envFile.readLines().forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                return@forEach
            }
            val equalsIndex = trimmed.indexOf('=')
            if (equalsIndex <= 0) {
                return@forEach
            }
            val key = trimmed.substring(0, equalsIndex).trim()
            val rawValue = trimmed.substring(equalsIndex + 1).trim()
            val value =
                when {
                    rawValue.startsWith('"') && rawValue.endsWith('"') && rawValue.length >= 2 ->
                        rawValue.substring(1, rawValue.lastIndex)
                    rawValue.startsWith('\'') && rawValue.endsWith('\'') && rawValue.length >= 2 ->
                        rawValue.substring(1, rawValue.lastIndex)
                    else -> rawValue
                }
            environment(key, value)
        }
    }
}
