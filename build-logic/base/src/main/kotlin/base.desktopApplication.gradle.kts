import base.DesktopApplicationConventionParams
import org.gradle.kotlin.dsl.getting
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("internal.config.desktop")
}

val desktopExtension = project.extensions.create(
    "configureDesktopApplication",
    DesktopApplicationConventionParams::class.java,
)

project.afterEvaluate {
    configure<ComposeExtension> {
        desktop {
            application {
                mainClass = desktopExtension.mainClass
                nativeDistributions {
                    targetFormats(*(desktopExtension.targetFormats.toTypedArray()))
                    packageName = desktopExtension.packageName
                    packageVersion = desktopExtension.packageVersion
                }
            }
        }
    }
}

configure<KotlinMultiplatformExtension> {
    sourceSets {
        kotlin {
            val desktopMain by getting {
                dependencies {
                    implementation(compose.desktop.currentOs)
                }
            }
        }
    }
}
