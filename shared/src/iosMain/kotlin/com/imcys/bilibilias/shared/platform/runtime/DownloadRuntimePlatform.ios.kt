package com.imcys.bilibilias.shared.platform.runtime

actual object DownloadRuntimePlatform {
    actual val maxSupportedConcurrentDownloads: Int = 2

    actual fun applyFfmpegRuntimeConfig(
        maxConcurrentDownloads: Int,
        enabledConcurrentMerge: Boolean,
    ) = Unit
}
