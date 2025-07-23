import extension.commonMainDependencies
import extension.ksp
import extension.libs

plugins {
    id("com.google.devtools.ksp")
    id("androidx.room")
}

commonMainDependencies {
    libs {
        implementation(bundles.room)
    }
}

dependencies {
    ksp(libs.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
