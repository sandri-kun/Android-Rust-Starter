package org.rust.starter

object NativeBridge {
    init {
        System.loadLibrary("android_binding")
    }

    external fun add(a: Int, b: Int): Int
    external fun greet(name: String): String
}