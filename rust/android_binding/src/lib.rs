use std::thread;
use std::time::Duration;

use jni::JNIEnv;
use jni::objects::{JClass, JObject, JString, JValue};
use jni::sys::{jint, jstring};

use core_engine;

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_add<'local>(
    _env: JNIEnv<'local>,
    _class: JClass<'local>,
    a: i32,
    b: i32,
) -> jint {
    core_engine::add(a, b)
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_greet<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    name: JString<'local>,
) -> jstring {
    let input: String = env
        .get_string(&name)
        .expect("Couldn't get java string!")
        .into();

    let output = core_engine::greet(&input);

    let output_jstring = env
        .new_string(output)
        .expect("Couldn't create java string!");

    output_jstring.into_raw()
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_doHeavyTaskWithProgress<'local>(
    mut env: JNIEnv<'local>,
    _class: JClass<'local>,
    listener: JObject<'local>,
) {
    for progress in 1..=100 {
        // 1. Simulasi pekerjaan berat di Rust
        thread::sleep(Duration::from_millis(30));

        // 2. Panggil method `onProgress(I)V` di Kotlin
        let result = env.call_method(
            &listener,
            "onProgress",
            "(I)V", // Signature: parameter Int (I), return Void (V)
            &[JValue::Int(progress as jint)],
        );

        // 3. Handling Keamanan: Hentikan loop jika terjadi exception/error di Kotlin
        if result.is_err() || env.exception_check().unwrap_or(false) {
            let _ = env.exception_clear();
            break;
        }
    }
}