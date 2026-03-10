plugins {
    `kotlin-dsl`
    kotlin("jvm").version(libs.versions.kotlin.get())
}

dependencies {
    libs {
        with(gradle) {
            implementation(kotlin)
            implementation(kotlinMultiplatform)
            implementation(kotlinJvm)
            implementation(composeCompiler)
            implementation(android)
            implementation(detekt)
            implementation(ksp)
        }
    }

    projects {
        implementation(gradleExtension)
    }
}
