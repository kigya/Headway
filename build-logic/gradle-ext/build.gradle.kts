plugins {
    kotlin("jvm").version(libs.versions.kotlin.get())
    `kotlin-dsl`
}

kotlin {
    explicitApi()
}

dependencies {
    libs {
        api(gradle.compose)
        implementation(gradleApi())
        implementation(kotlin("gradle-plugin"))
        api(files(javaClass.superclass.protectionDomain.codeSource.location))
    }
}
