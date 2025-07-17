import base.DesktopApplicationConventionParams
import extension.libs
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.compose")
}

val desktopExtension = project.extensions.create(
    "configureDesktopApplication",
    DesktopApplicationConventionParams::class.java,
)

configure<KotlinMultiplatformExtension> {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(libs.versions.java.get()))
        }
    }
}

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
