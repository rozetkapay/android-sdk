plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.parcelize) apply false
    alias(libs.plugins.serialization) apply false
}

buildscript {
    extra.apply {
        set("minSdk", 23)
        set("targetSdk", 35)
        set("compileSdk", 35)

        set("versionCode", 1)
        set("versionName", "0.1-alpha03")
    }
}
