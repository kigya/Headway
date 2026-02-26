import base.AndroidApplicationConventionParams
import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import extension.configureIfExists
import extension.getInt
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

val androidExtension = project.extensions.create(
    "configureAndroidApplication",
    AndroidApplicationConventionParams::class.java
)

androidExtension.namespace.convention(
    project.provider {
        val projectNameFormatted = project.path
            .drop(1)
            .replace(Regex("[-:]"), ".")
        "dev.kigya.headway.$projectNameFormatted"
    }
)

/**
 * Before using this plugin, ensure that necessary Android configurations have been applied.
 * Note: This script does not configure the Kotlin JVM version.
 */
configure<BaseExtension> {
    compileSdkVersion(rootProject.libs.versions.compileSdk.getInt())

    defaultConfig {
        namespace = androidExtension.namespace.get()
        println("Namespace: ${project.path} -> $namespace")

        minSdk = rootProject.libs.versions.minSdk.getInt()
        targetSdk = rootProject.libs.versions.targetSdk.getInt()
        versionCode = androidExtension.versionCode.get()
        versionName = androidExtension.versionName.get()
        resourceConfigurations += androidExtension.resourceConfigurations.get()

        testOptions.unitTests.apply {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    compileOptions {
        sourceCompatibility(rootProject.libs.versions.java.get())
        targetCompatibility(rootProject.libs.versions.java.get())
    }

    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE.txt",
                "META-INF/{AL2.0,LGPL2.1}",
                "kotlin/coroutines/coroutines.kotlin_builtins",
            )
        )
    }
}

configureIfExists(CommonExtension::class.java) {
    lint {
        htmlReport = false
        baseline = file("${rootProject.projectDir}/lint-baseline.xml")
    }
}

configure<KotlinMultiplatformExtension> {
    androidTarget {
        tasks.withType<KotlinJvmCompile>().configureEach {
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
            }
        }
    }
}
