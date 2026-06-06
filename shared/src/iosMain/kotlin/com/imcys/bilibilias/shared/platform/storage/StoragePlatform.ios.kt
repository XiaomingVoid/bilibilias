package com.imcys.bilibilias.shared.platform.storage

import com.imcys.bilibilias.shared.util.StorageInfoData
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSFileSize
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.UIKit.UIApplication

actual object StoragePlatform {
    actual suspend fun getStorageInfoData(): StorageInfoData {
        val total = getFileSystemValue(platform.Foundation.NSURLVolumeTotalCapacityKey.toString())
        val available = getFileSystemValue(platform.Foundation.NSURLVolumeAvailableCapacityKey.toString())
        val appBytes = getAppUsedBytes()
        val cacheTotalBytes = getCacheTotalBytes()
        return StorageInfoData(
            totalBytes = total,
            usedBytes = (total - available).coerceAtLeast(0),
            availableBytes = available,
            appBytes = appBytes,
            downloadBytes = 0L,
            cacheTotalBytes = cacheTotalBytes
        )
    }

    actual suspend fun hasDownloadSAFPermission(): Boolean = true

    actual fun clearCache(): Boolean {
        val cacheDirectory = getDirectory(NSCachesDirectory)
        return if (cacheDirectory == null) {
            false
        } else {
            deleteChildren(cacheDirectory.path.orEmpty())
        }
    }

    actual fun openDownloadDirectory(): Boolean {
        val url = NSURL.fileURLWithPath(NSHomeDirectory())
        return UIApplication.sharedApplication.canOpenURL(url).also { canOpen ->
            if (canOpen) {
                UIApplication.sharedApplication.openURL(url)
            }
        }
    }

    actual fun restartApplication() {
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getFileSystemValue(key: String): Long {
        val homeUrl = NSURL.fileURLWithPath(NSHomeDirectory())
        val values = homeUrl.resourceValuesForKeys(listOf(key), error = null)
        return (values?.get(key) as? Number)?.toLong() ?: -1L
    }

    private fun getAppUsedBytes(): Long {
        val directories = listOfNotNull(
            getDirectory(NSApplicationSupportDirectory)?.path,
            getDirectory(NSCachesDirectory)?.path
        )
        return directories.sumOf { getFolderSize(it.orEmpty()) }
    }

    private fun getCacheTotalBytes(): Long {
        return getFolderSize(getDirectory(NSCachesDirectory)?.path.orEmpty())
    }

    private fun getDirectory(directory: ULong): NSURL? {
        return NSFileManager.defaultManager.URLsForDirectory(
            directory = directory,
            inDomains = NSUserDomainMask
        ).firstOrNull() as? NSURL
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getFolderSize(path: String): Long {
        if (path.isEmpty()) return 0L
        val fileManager = NSFileManager.defaultManager
        val exists = fileManager.fileExistsAtPath(path)
        if (!exists) return 0L
        val attributes = fileManager.attributesOfItemAtPath(path, error = null)
        val fileSize = (attributes?.get(NSFileSize) as? Number)?.toLong() ?: 0L
        val enumerator = fileManager.enumeratorAtPath(path) ?: return fileSize
        var size = fileSize
        while (true) {
            val item = enumerator.nextObject() as? String ?: break
            val childPath = "$path/$item"
            val childAttributes = fileManager.attributesOfItemAtPath(childPath, error = null)
            size += (childAttributes?.get(NSFileSize) as? Number)?.toLong() ?: 0L
        }
        return size
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun deleteChildren(path: String): Boolean {
        if (path.isEmpty()) return false
        val fileManager = NSFileManager.defaultManager
        val children = fileManager.contentsOfDirectoryAtPath(path, error = null) ?: return true
        var success = true
        children.forEach { child ->
            val childPath = "$path/$child"
            success = fileManager.removeItemAtPath(childPath, error = null) && success
        }
        return success
    }
}
