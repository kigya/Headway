import extension.getInt
import extension.libs

plugins {
    `java-library`
    kotlin("jvm")
    id("com.android.lint")
    id("internal.config.kotlin.detekt")
}

java {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.java.getInt())
    targetCompatibility = JavaVersion.toVersion(libs.versions.java.getInt())
}
