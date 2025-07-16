 import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.BaseExtension
import extension.configureIfExists

plugins {
    id("internal.config.android")
    id("internal.config.kotlin.detekt")
}

configure<BaseExtension> {

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}

configureIfExists(CommonExtension::class.java) {
    lint {
        htmlReport = false
        baseline = file("${project.projectDir}/lint-baseline.xml")
    }
}
