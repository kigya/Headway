import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    id("internal.config.detekt")
}

configure<DetektExtension> {
    source.from(
        "src/androidMain/kotlin",
        "src/commonMain/kotlin",
        "src/iosMain/kotlin",
        "src/desktopMain/kotlin",
    )
}
