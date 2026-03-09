import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.projects

plugins {
    `kotlin-dsl`
}

dependencies {
    libs {
        with(gradle) {
            implementation(kotlin)
            implementation(serialization)
            implementation(detekt)
        }
    }

    projects {
        implementation(gradleExt)
    }
}
