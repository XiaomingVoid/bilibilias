package com.imcys.bilibilias.shared.util

import com.imcys.bilibilias.shared.platform.runtime.format

data class StorageInfoData(
    val totalBytes: Long,
    val usedBytes: Long,
    val availableBytes: Long,
    val appBytes: Long,
    val downloadBytes: Long,
    val cacheTotalBytes: Long
)

object StorageUtil {
    /**
     * 将字节数转换为合适的单位（MB、GB、TB），返回最小满足的单位字符串（大写）。
     * 例如：不满1GB则返回多少MB，不满1MB则返回多少KB。
     */
    fun formatSize(bytes: Long): String {
        val kb = 1024L
        val mb = kb * 1024
        val gb = mb * 1024
        val tb = gb * 1024
        return when {
            bytes >= tb -> format("%.2f TB", bytes / tb.toDouble())
            bytes >= gb -> format("%.2f GB", bytes / gb.toDouble())
            bytes >= mb -> format("%.2f MB", bytes / mb.toDouble())
            bytes >= kb -> format("%.2f KB", bytes / kb.toDouble())
            else -> "$bytes B"
        }
    }
}
