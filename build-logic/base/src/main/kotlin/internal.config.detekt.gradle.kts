import detekt.DetektConfigs
import extension.detektPlugins
import extension.libs
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("io.gitlab.arturbosch.detekt")
}

val detektExcludes = listOf(
    "**/dev/kigya/headway/navigation/api/navigator/AsyncRunner.kt",
)

configure<DetektExtension> {
    config.from(
        rootProject.file(DetektConfigs.MAIN),
        rootProject.file(DetektConfigs.COMPOSE)
    )
    autoCorrect = System.getProperty("DETEKT_AUTOCORRECT")?.toBooleanStrictOrNull() ?: true
    parallel = true
    allRules = false
    debug = true

    source.from(
        "src/commonMain/kotlin",
        "src/androidMain/kotlin",
        "src/iosMain/kotlin",
        "src/desktopMain/kotlin",
        "src/webMain/kotlin",
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
    exclude(detektExcludes)
}

tasks.withType<DetektCreateBaselineTask>().configureEach {
    jvmTarget = libs.versions.java.get()
    exclude(detektExcludes)
}

dependencies {
    detektPlugins(libs.detekt.formatting)
    detektPlugins(libs.detekt.composePluginLopez)
    detektPlugins(libs.detekt.composePluginKode)
}
