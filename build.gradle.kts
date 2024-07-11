plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.parcelize) apply false
}

buildscript {
    extra.apply {
        set("minSdk", 26)
        set("targetSdk", 34)
        set("compileSdk", 34)

        set("versionCode", 1)
        set("versionName", "0.1-alpha01")
    }
}

object ProjectConstants {
    const val minSdk =  26
    const val targetSdk =  34
    const val compileSdk =  34
}
