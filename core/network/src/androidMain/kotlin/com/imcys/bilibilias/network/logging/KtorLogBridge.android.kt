package com.imcys.bilibilias.network.logging

import android.util.Log

actual object KtorLogBridge {
    private const val TAG = "Ktor"

    actual fun log(message: String) {
        message.lineSequence().forEach { line ->
            Log.d(TAG, line)
        }
    }
}
