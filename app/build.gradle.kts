import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "org.rust.starter"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "org.rust.starter"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}

// Task untuk kompilasi Rust menggunakan cargo-ndk
val buildRust = tasks.register<Exec>("buildRust") {
    description = "Compile Rust library for Android targets using cargo-ndk"
    workingDir = file("../rust") // Path ke workspace Rust Anda

    // Deteksi OS untuk eksekusi command
    val isWindows = DefaultNativePlatform.getCurrentOperatingSystem().isWindows
    val executableName = if (isWindows) "cargo.exe" else "cargo"

    // Command: cargo ndk -t arm64-v8a -t armeabi-v7a -t x86_64 -o <jniLibs_path> build --release
    commandLine(
        executableName, "ndk",
        "-t", "arm64-v8a",
        "-t", "armeabi-v7a",
        "-t", "x86_64",
        "-o", "${projectDir}/src/main/jniLibs",
        "build",
        "--release"
    )
}

// Otomatis jalankan task Rust sebelum Android melakukan merge JNI Libs/PreBuild
tasks.whenTaskAdded {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) {
        dependsOn(buildRust)
    }
}