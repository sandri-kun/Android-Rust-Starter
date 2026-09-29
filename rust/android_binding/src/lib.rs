use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;
use core_engine;

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_add(
    _env: JNIEnv,
    _class: JClass,
    a: i32,
    b: i32,
) -> i32 {
    core_engine::add(a, b)
}

#[unsafe(no_mangle)]
pub extern "system" fn Java_org_rust_starter_NativeBridge_greet(
    mut env: JNIEnv,
    _class: JClass,
    name: JString,
) -> jstring {
    let input: String = env
        .get_string(&name)
        .expect("Couldn't get java string!")
        .into();
    let output = core_engine::greet(&input);
    env.new_string(output)
        .expect("Couldn't create java string!")
        .into_raw()
}