plugins {
    `kotlin-dsl`
}

dependencies {
    with(libs.gradle) {
        implementation(android)
    }

    with(projects) {
        implementation(gradleExt)
    }
}
