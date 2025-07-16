import extension.compose
import extension.debugImplementation
import extension.implementation

plugins {
    id("component.android.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

dependencies {
    with(compose) {
        implementation(preview)
        debugImplementation(uiTooling)
    }
}
