# 🚀 Android Rust Starter

[![Rust](https://img.shields.io/badge/Rust-v1.80%2B-orange.svg?style=flat-square&logo=rust)](https://www.rust-lang.org/)
[![Android NDK](https://img.shields.io/badge/Android%20NDK-Side--by--Side-green.svg?style=flat-square&logo=android)](https://developer.android.com/ndk)
[![JNI Version](https://img.shields.io/badge/JNI%20Crate-v0.22%2B-blue.svg?style=flat-square)](https://crates.io/crates/jni)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=flat-square)](LICENSE)

A modern, high-performance starter template for integrating **Rust** into **Android** applications using **Cargo Workspaces**, **JNI (v0.22+)**, and **`cargo-ndk`** automated Gradle tasks.

---

## 🌟 Features

* **⚡ Modular Cargo Workspace**: Clean separation between pure Rust business logic (`core_engine`) and Android FFI bindings (`android_binding`).
* **🤖 Automated Gradle Build**: Native libraries (`.so`) are automatically compiled via `cargo-ndk` directly during the Android build lifecycle.
* **🔒 Safe & Modern JNI Integration**: Built with `jni` crate v0.22+, utilizing `EnvUnowned` for thread-safe native execution.
* **📱 Multi-Architecture Support**: Pre-configured target ABIs for `arm64-v8a`, `armeabi-v7a`, and `x86_64`.
* **🧹 Clean Git Tracking**: Hierarchical `.gitignore` setups ensuring native binaries, `target/` builds, and IDE caches stay out of your repository.

---

## 📁 Project Structure

```text
AndroidRustStarter/
├── app/                          # Android Application Module (Kotlin / Gradle)
│   ├── build.gradle.kts          # Contains `buildRust` task integrated into Gradle build
│   └── src/main/
│       ├── java/org/rust/starter/
│       │   └── NativeBridge.kt   # Kotlin JNI Bridge
│       └── jniLibs/              # Output folder for compiled Rust .so files (Ignored in Git)
│
└── rust/                         # Cargo Workspace Root
    ├── Cargo.toml                # Workspace definition (resolver = "3")
    ├── core_engine/              # Pure Rust logic (Math, Crypto, Business Logic)
    └── android_binding/          # FFI Layer (JNI functions exported to Kotlin)
```

---

## 🛠️ Prerequisites

1. **Android Studio** with **Android NDK (Side by side)** installed via SDK Manager.
2. **Rust Toolchain**: Install via [rustup.rs](https://rustup.rs/).
3. **Android Targets for Rust**:
   ```bash
   rustup target add aarch64-linux-android armv7-linux-androideabi x86_64-linux-android
   ```
4. **`cargo-ndk`**:
   ```bash
   cargo install cargo-ndk
   ```

---

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone [https://github.com/sandri-kun/Android-Rust-Starter.git](https://github.com/sandri-kun/Android-Rust-Starter.git)
cd Android-Rust-Starter
```

### 2. Verify Rust & Cargo Setup
```bash
cd rust
cargo check
```

### 3. Open in Android Studio & Run
1. Open the root folder `AndroidRustStarter` in Android Studio.
2. **Switch Project View**:
   > [!TIP]
   > **How to view the Rust Workspace:** In the top-left Project pane, change the view mode from **Android** to **Project**. The default **Android** view filters out non-Android folders, so the `rust/` workspace will only be visible when set to **Project** view.
3. Ensure your NDK path is detected in `local.properties`.
4. Select the **`app`** configuration in the toolbar and hit **Run (Shift + F10)**.

> 💡 The Gradle task `buildRust` will automatically execute `cargo ndk` to compile the native libraries into `app/src/main/jniLibs/` before APK packaging.

---

## 🔧 How the Automated Build Works

Instead of relying on unmaintained Gradle plugins, this project uses a custom `Exec` task in `app/build.gradle.kts`:

```kotlin
val buildRust = tasks.register<Exec>("buildRust") {
    description = "Compile Rust library for Android targets using cargo-ndk"
    workingDir = file("../rust")

    val isWindows = org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
        .getCurrentOperatingSystem().isWindows
    val executableName = if (isWindows) "cargo.exe" else "cargo"

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

tasks.whenTaskAdded {
    if (name.startsWith("merge") && name.endsWith("JniLibFolders")) {
        dependsOn(buildRust)
    }
}
```

---

## 💻 Code Snippet Example

### Rust (`rust/android_binding/src/lib.rs`)
```rust
use jni::EnvUnowned;
use jni::objects::{JClass, JString};
use jni::sys::{jint, jstring};

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_add<'local>(
    _unowned_env: EnvUnowned<'local>,
    _class: JClass<'local>,
    a: i32,
    b: i32,
) -> jint {
    a + b
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_greet<'local>(
    mut unowned_env: EnvUnowned<'local>,
    _class: JClass<'local>,
    name: JString<'local>,
) -> jstring {
    let outcome = unowned_env.with_env(|env| {
        #[allow(deprecated)]
        let java_str = env.get_string(&name).expect("Failed to read JNI String");
        let name_str: String = java_str.into();

        let greeting = format!("Hello {}, greetings from Rust!", name_str);

        let output_jstring = env.new_string(greeting).expect("Failed to create JString");
        Ok::<jstring, jni::errors::Error>(output_jstring.into_raw())
    });

    match outcome.outcome() {
        Ok(raw_jstring) => raw_jstring,
        Err(_) => std::ptr::null_mut(),
    }
}
```

### Kotlin Bridge (`app/src/main/java/org/rust/starter/NativeBridge.kt`)
```kotlin
package org.rust.starter

object NativeBridge {
    init {
        System.loadLibrary("android_binding")
    }

    external fun add(a: Int, b: Int): Int
    external fun greet(name: String): String
}
```

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](https://github.com/sandri-kun/Android-Rust-Starter/issues).

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.