plugins {
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)
}

kotlin {
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }
}
