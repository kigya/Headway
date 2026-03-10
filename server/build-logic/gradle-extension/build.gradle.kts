
plugins {
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
        implementation(gradleApi())
        implementation(kotlin("gradle-plugin"))
        api(files(javaClass.superclass.protectionDomain.codeSource.location))
    }
}
