plugins {
    `kotlin-dsl`
}

dependencies {
    with(libs) {
        with(gradle) {
            implementation(kotlin)
            implementation(kotlin.multiplatform)
            implementation(compose.compiler)
            implementation(android)
            implementation(detekt)
            implementation(ksp)
        }
    }

    with(projects) {
        implementation(gradleExt)
    }
}
