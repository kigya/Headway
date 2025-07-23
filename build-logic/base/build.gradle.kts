plugins {
    `kotlin-dsl`
    kotlin("jvm").version(libs.versions.kotlin.get())
}

dependencies {
    libs {
        implementation(gradle.kotlin)
        implementation(gradle.kotlinMultiplatform)
        implementation(gradle.composeCompiler)
        implementation(gradle.android)
        implementation(gradle.detekt)
        implementation(gradle.ksp)
    }

    projects {
        implementation(gradleExt)
    }
}
