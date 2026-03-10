import base.WebApplicationConventionParams
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import kotlin.apply
import kotlin.jvm.java

plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("internal.config.detekt")
}

val webExtension = project.extensions.create(
    "configureWebApplication",
    WebApplicationConventionParams::class.java
)

project.afterEvaluate {
    configure<KotlinMultiplatformExtension> {
        wasmJs {
            val name = webExtension.name.get().lowercase()
            outputModuleName.set("${name}App")
            browser {
                val rootDirPath = project.rootDir.path
                val projectDirPath = project.projectDir.path
                commonWebpackConfig {
                    outputFileName = "${name}App.js"
                    devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                        static = (static ?: mutableListOf()).apply {
                            add(rootDirPath)
                            add(projectDirPath)
                        }
                    }
                }
            }
            binaries.executable()
        }
    }
}
