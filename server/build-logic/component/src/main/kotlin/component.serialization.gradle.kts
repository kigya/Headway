import extension.implementation
import extension.libs

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    dependencies {
        implementation(libs.serializationJson)
    }
}
