import extension.commonMainDependencies
import extension.libs

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

commonMainDependencies {
    libs {
        implementation(serializationJson)
    }
}
