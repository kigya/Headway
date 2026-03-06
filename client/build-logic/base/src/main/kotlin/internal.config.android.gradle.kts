import base.AndroidApplicationConventionParams
import com.android.build.api.dsl.ApplicationExtension
import extension.configureIfExists
import extension.getInt
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

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
 * Android application module configuration.
 * Uses ApplicationExtension (AGP 9+) instead of the deprecated BaseExtension.
 * Note: This plugin is only applied to the Android app module (com.android.application).
 */
configureIfExists(ApplicationExtension::class.java) {
    namespace = androidExtension.namespace.get()
    println("Namespace: ${project.path} -> $namespace")

    compileSdk = rootProject.libs.versions.compileSdk.getInt()

    defaultConfig {
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

    sourceSets.getByName("main") {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.directories.add("src/androidMain/res")
        java.directories.add("src/androidMain/kotlin")
        kotlin.directories.add("src/androidMain/kotlin")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
        targetCompatibility = JavaVersion.toVersion(rootProject.libs.versions.java.get())
    }

    packaging {
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

    lint {
        htmlReport = false
        baseline = file("${rootProject.projectDir}/lint-baseline.xml")
    }
}

configureIfExists(KotlinAndroidProjectExtension::class.java) {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget(rootProject.libs.versions.java.get()))
    }
}
