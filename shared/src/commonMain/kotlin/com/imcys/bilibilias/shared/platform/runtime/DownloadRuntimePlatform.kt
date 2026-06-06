package com.imcys.bilibilias.shared.platform.runtime

expect object DownloadRuntimePlatform {
    val maxSupportedConcurrentDownloads: Int

    fun applyFfmpegRuntimeConfig(
        maxConcurrentDownloads: Int,
        enabledConcurrentMerge: Boolean,
    )
}
