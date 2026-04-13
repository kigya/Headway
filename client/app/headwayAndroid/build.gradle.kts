plugins {
    alias(libs.plugins.convention.base.androidApplication)
    alias(libs.plugins.convention.component.compose)

    alias(libs.plugins.convention.component.koin)
}

configureAndroidApplication {
    namespace.set("dev.kigya.headway")
    versionCode.set(1)
    versionName.set("1.0.0")
    resourceConfigurations.addAll(listOf("en", "ru"))
}

dependencies {
    projects {
        implementation(core.outcome)
        implementation(core.sessionInternal)
        implementation(di.diModules)
        implementation(shared)
    }
    libs {
        implementation(koin.android)
    }
}
