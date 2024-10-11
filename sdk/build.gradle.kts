import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.parcelize)
    alias(libs.plugins.serialization)
    `maven-publish`
}

android {
    namespace = "com.rozetkapay.sdk"
    compileSdk = rootProject.extra["compileSdk"] as Int

    defaultConfig {
        minSdk = rootProject.extra["minSdk"] as Int
        aarMetadata {
            minCompileSdk = rootProject.extra["minSdk"] as Int
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "VERSION", "\"${rootProject.extra["versionName"]}\"")
    }

    @Suppress("UnstableApiUsage")
    testFixtures {
        enable = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    android.libraryVariants.all {
        val variant = this
        variant.outputs
            .map { it as com.android.build.gradle.internal.api.BaseVariantOutputImpl }
            .forEach { output ->
                val outputFileName = "rozetka-pay-sdk-" + (rootProject.extra["versionName"] as String) + ".aar"
                output.outputFileName = outputFileName
            }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.kotlin.datetime)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.android.compose)
    implementation(libs.bundles.ktor)
    implementation(libs.kotlin.serialization)
    implementation(libs.google.pay)
    implementation(libs.google.pay.compose)
    implementation(libs.kotlin.coroutines.play.services)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

tasks.register("printVersion") {
    group = "versioning"
    description = "Prints the version of the sdk library"

    doLast {
        println(rootProject.extra["versionName"] as String)
    }
}

// publishing

val githubProperties = File(rootDir, "github.properties").let { file ->
    Properties().apply {
        if (file.exists()) file.inputStream().use { load(it) }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.rozetkapay"
            artifactId = "sdk"
            version = rootProject.extra["versionName"] as String
            pom {
                name.set("rozetka-pay-sdk")
                url.set("https://github.com/rozetkapay/android-sdk")
            }
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/rozetkapay/android-sdk")
            credentials {
                username = githubProperties["github_user"] as String? ?: System.getenv("GITHUB_USER")
                password = githubProperties["github_token"] as String? ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
    repositories {
        maven {
            name = "LocalDirectory"
            url = uri(layout.buildDirectory.dir("repository"))
        }
    }
}
