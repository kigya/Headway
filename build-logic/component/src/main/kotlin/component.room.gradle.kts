import extension.ksp
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("com.google.devtools.ksp")
    id("androidx.room")
}

configure<KotlinMultiplatformExtension> {
    sourceSets {
        commonMain.dependencies {
            with(libs) {
                implementation(bundles.room)
            }
        }
    }
}

dependencies {
    ksp(libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
