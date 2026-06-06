package com.imcys.bilibilias.network.logging

actual object KtorLogBridge {
    actual fun log(message: String) {
        println(message)
    }
}
