import extension.commonMainDependencies
import extension.implementation
import extension.libs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")

    extensions.configure(KotlinMultiplatformExtension::class.java) {
        commonMainDependencies {
            libs {
                implementation(serializationJson)
            }
        }
    }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    pluginManager.apply("org.jetbrains.kotlin.plugin.serialization")
    dependencies {
        implementation(libs.serializationJson)
    }
}
