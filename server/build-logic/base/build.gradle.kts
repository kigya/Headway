plugins {
    `kotlin-dsl`
    kotlin("jvm").version(libs.versions.kotlin.get())
}

dependencies {
    libs {
        with(gradle) {
            implementation(kotlin)
            implementation(detekt)
        }
    }

    projects {
        implementation(gradleExt)
    }
}
