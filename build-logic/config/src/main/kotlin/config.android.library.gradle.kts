plugins {
    id("config.android")
    id("com.android.library")
}

android {
    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}
