package com.imcys.bilibilias.shared.platform.storage


import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.imcys.bilibilias.shared.platform.runtime.koinApplication
import java.io.File


actual object FileSystem {
    actual fun openFile(fileInfo: FileInfo): Boolean {
        val context = koinApplication
        val savePath = fileInfo.path

        val (uri, type) = if (savePath.startsWith("content://")) {
            savePath.toUri() to (context.contentResolver.getType(Uri.parse(savePath)) ?: "")
        } else {
            val file = File(savePath)
            val fileUri = runCatching {
                FileProvider.getUriForFile(
                    context,
                    "${context.applicationContext.packageName}.provider",
                    file
                )
            }.getOrNull() ?: return false

            fileUri to (context.contentResolver.getType(fileUri) ?: "")
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            setDataAndType(uri, type)
        }

        return runCatching {
            context.startActivity(intent)
            true
        }.getOrDefault(false)
    }

    actual fun fileExists(path: String): Boolean {
        return if (path.startsWith("content://")) {
            // content URI 无法直接判断存在性，默认认为存在
            true
        } else {
            File(path).exists()
        }
    }

    actual fun deleteFile(path: String): DeleteResult {
        val context = koinApplication

        return runCatching {
            if (path.startsWith("content://")) {
                val uri = Uri.parse(path)
                val rows = context.contentResolver.delete(uri, null, null)
                if (rows > 0) DeleteResult.SUCCESS else DeleteResult.FAILED
            } else {
                val file = File(path)
                when {
                    !file.exists() -> DeleteResult.FILE_NOT_EXIST
                    file.delete() -> DeleteResult.SUCCESS
                    else -> DeleteResult.FAILED
                }
            }
        }.getOrElse { DeleteResult.FAILED }
    }

    actual enum class DeleteResult { SUCCESS, FILE_NOT_EXIST, FAILED }
}