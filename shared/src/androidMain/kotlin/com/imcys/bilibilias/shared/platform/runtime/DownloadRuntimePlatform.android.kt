package com.imcys.bilibilias.shared.platform.runtime

import android.app.ActivityManager
import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKitConfig
import org.koin.mp.KoinPlatform

actual object DownloadRuntimePlatform {
    actual val maxSupportedConcurrentDownloads: Int
        get() {
            val context = KoinPlatform.getKoin().get<Context>()
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val isLowRamDevice = activityManager?.isLowRamDevice ?: false
            val memoryClass = activityManager?.memoryClass ?: 128
            val processors = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)

            return when {
                isLowRamDevice || memoryClass <= 128 || processors <= 4 -> 2
                memoryClass <= 192 || processors <= 6 -> 3
                memoryClass <= 256 || processors <= 8 -> 4
                else -> 5
            }
        }

    actual fun applyFfmpegRuntimeConfig(
        maxConcurrentDownloads: Int,
        enabledConcurrentMerge: Boolean,
    ) {
        val ffmpegConcurrency = if (enabledConcurrentMerge && maxConcurrentDownloads > 1) {
            maxConcurrentDownloads
        } else {
            1
        }

        FFmpegKitConfig.setAsyncConcurrencyLimit(ffmpegConcurrency)
        FFmpegKitConfig.setSessionHistorySize(ffmpegConcurrency)
    }
}
