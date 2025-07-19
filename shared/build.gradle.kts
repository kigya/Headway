plugins {
    with(libs.plugins.convention) {
        alias(base.sharedLibrary)
        alias(component.compose)
    }
}
