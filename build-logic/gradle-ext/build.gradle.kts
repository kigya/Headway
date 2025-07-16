plugins {
    `kotlin-dsl`
}

dependencies {
    with(libs) {
        api(gradle.compose)
        api(files(javaClass.superclass.protectionDomain.codeSource.location))
    }
}
