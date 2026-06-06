package com.imcys.bilibilias.shared.platform.storage


import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual object FileSystem {
    actual fun openFile(fileInfo: FileInfo): Boolean {
        val filePath = fileInfo.path
        val url = NSURL.fileURLWithPath(filePath)

        if (UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
            return true
        }

        return false
    }

    actual fun fileExists(path: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path)
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun deleteFile(path: String): DeleteResult {
        return runCatching {
            val fileManager = NSFileManager.defaultManager
            val exists = fileManager.fileExistsAtPath(path)

            if (!exists) {
                DeleteResult.FILE_NOT_EXIST
            } else {
                val success = fileManager.removeItemAtPath(path, null)
                if (success) DeleteResult.SUCCESS else DeleteResult.FAILED
            }
        }.getOrElse { DeleteResult.FAILED }
    }

    actual enum class DeleteResult { SUCCESS, FILE_NOT_EXIST, FAILED }
}