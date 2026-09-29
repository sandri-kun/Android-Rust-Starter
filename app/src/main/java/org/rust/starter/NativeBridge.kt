package org.rust.starter

fun interface ProgressListener {
    fun onProgress(progress: Int)
}

object NativeBridge {
    init {
        System.loadLibrary("android_binding")
    }

    external fun add(a: Int, b: Int): Int
    external fun greet(name: String): String
    external fun doHeavyTaskWithProgress(listener: ProgressListener)
}