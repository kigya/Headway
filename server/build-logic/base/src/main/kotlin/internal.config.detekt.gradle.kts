import detekt.DetektConfigs
import extension.detektPlugins
import extension.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

configure<DetektExtension> {
    config.from(rootProject.file(DetektConfigs.BACKEND))
    autoCorrect = System.getProperty("DETEKT_AUTOCORRECT")?.toBooleanStrictOrNull() ?: true
    parallel = true
    allRules = false
    debug = true

    source.from(
        "src/main/kotlin",
        "src/main/java",
    )
}

tasks.withType<Detekt>().configureEach {
    jvmTarget = libs.versions.java.get()
    reports {
        html.required.set(true)
        xml.required.set(false)
        txt.required.set(false)
        sarif.required.set(false)
        md.required.set(false)
    }
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = libs.versions.java.get()
}

dependencies {
    detektPlugins(libs.detekt.formatting)
}
