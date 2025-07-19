import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.projects

plugins {
    `kotlin-dsl`
}

dependencies {
    with(libs) {
        with(gradle) {
            implementation(kotlin)
            implementation(kotlinx.serialization)
            implementation(ksp)
            implementation(android)
            implementation(detekt)
            implementation(koin)
            implementation(compose.compiler)
            implementation(room)
        }
    }

    with(projects) {
        implementation(gradleExt)
    }
}
