package com.imcys.bilibilias.network.utils

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.time

@OptIn(ExperimentalForeignApi::class)
internal actual fun platformEpochMillis(): Long = time(null).toLong() * 1000L
