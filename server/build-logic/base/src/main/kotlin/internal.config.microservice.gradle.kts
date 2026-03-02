import extension.enableContextParameters

plugins {
    id("org.jetbrains.kotlin.jvm")
    application
    id("internal.config.detekt")
}

enableContextParameters()
