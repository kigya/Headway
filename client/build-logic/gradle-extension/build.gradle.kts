plugins {
    kotlin("jvm").version(libs.versions.kotlin.get())
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-parameters")
    }
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
