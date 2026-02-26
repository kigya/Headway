import extension.wasmMainDependencies

plugins {
    alias(libs.plugins.convention.base.webApplication)
    alias(libs.plugins.convention.base.sharedLibrary)
    alias(libs.plugins.convention.component.compose)
}

configureWebApplication {
    name.set("Headway")
}

wasmMainDependencies {
    projects {
        implementation(shared)
    }
}
