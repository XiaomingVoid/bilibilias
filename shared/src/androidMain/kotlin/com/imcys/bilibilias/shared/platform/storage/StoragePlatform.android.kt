package com.imcys.bilibilias.shared.platform.storage

import android.app.usage.StorageStatsManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.DocumentsContract
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import com.imcys.bilibilias.shared.util.StorageInfoData
import com.imcys.bilibilias.data.repository.AppSettingsRepository
import com.imcys.bilibilias.datastore.downloadUri
import com.imcys.bilibilias.shared.platform.runtime.koinApplication
import kotlinx.coroutines.flow.first
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.util.UUID
import kotlin.system.exitProcess

actual object StoragePlatform {
    private val appSettingsRepository: AppSettingsRepository
        get() = getKoin().get()

    actual suspend fun getStorageInfoData(): StorageInfoData {
        val total: Long
        val avail: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val sm = koinApplication.getSystemService(android.content.Context.STORAGE_SERVICE) as StorageManager
                val stats =
                    koinApplication.getSystemService(android.content.Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                val primaryVolume: StorageVolume = sm.primaryStorageVolume
                val dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    primaryVolume.directory!!
                } else {
                    Environment.getExternalStorageDirectory()
                }
                val uuid: UUID = sm.getUuidForPath(dir)
                total = stats.getTotalBytes(uuid)
                val statFs = StatFs(dir.absolutePath)
                avail = statFs.availableBytes
            } catch (e: Exception) {
                return StorageInfoData(-1L, -1L, -1L, -1L, -1L, -1L)
            }
        } else {
            val path = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.absolutePath)
            total = stat.totalBytes
            avail = stat.availableBytes
        }

        val used = total - avail
        val appBytes = getAppUsedBytes()
        val downloadBytes = getDownloadFolderSize()
        val cacheTotalBytes = getCacheTotalBytes()
        return StorageInfoData(total, used, avail, appBytes, downloadBytes, cacheTotalBytes)
    }

    actual suspend fun hasDownloadSAFPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return true
        val uriString = appSettingsRepository.appSettingsFlow.first().downloadUri
        if (uriString.isEmpty()) return false
        val docFile = DocumentFile.fromTreeUri(koinApplication, uriString.toUri())
        return docFile != null && docFile.exists() && docFile.isDirectory
    }

    actual fun clearCache(): Boolean {
        val cacheDirs = listOf(
            koinApplication.cacheDir,
            koinApplication.externalCacheDir,
            koinApplication.getExternalFilesDir("video"),
            koinApplication.getExternalFilesDir("audio")
        )
        var allSuccess = true
        cacheDirs.forEach { dir ->
            allSuccess = allSuccess && deleteFolderRecursively(dir)
            if (dir != null && !dir.exists()) dir.mkdirs()
        }
        return allSuccess
    }

    actual fun openDownloadDirectory(): Boolean {
        val targetDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "BILIBILIAS"
        ).apply {
            if (!exists()) mkdirs()
        }
        val relativePath = targetDir.absolutePath.substringAfter("/storage/emulated/0/")
        val downloadUri = DocumentsContract.buildDocumentUri(
            "com.android.externalstorage.documents",
            "primary:$relativePath"
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(downloadUri, "vnd.android.document/directory")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        return try {
            koinApplication.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }

    actual fun restartApplication() {
        val packageManager = koinApplication.packageManager
        val intent = packageManager.getLaunchIntentForPackage(koinApplication.packageName)
        val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        koinApplication.startActivity(mainIntent)
        exitProcess(0)
    }

    private fun getFolderSize(file: File?): Long {
        if (file == null || !file.exists()) return 0L
        if (file.isFile) return file.length()
        var size = 0L
        file.listFiles()?.forEach {
            size += getFolderSize(it)
        }
        return size
    }

    private fun getAppUsedBytes(): Long {
        var size = 0L
        val dirs = listOf(
            koinApplication.filesDir,
            koinApplication.cacheDir,
            koinApplication.codeCacheDir,
            koinApplication.externalCacheDir,
            koinApplication.getExternalFilesDir(null)
        )
        dirs.forEach { size += getFolderSize(it) }
        return size
    }

    private fun getDocumentFileSize(docFile: DocumentFile?): Long {
        if (docFile == null || !docFile.exists()) return 0L
        if (docFile.isFile) return docFile.length()
        var size = 0L
        docFile.listFiles().forEach { size += getDocumentFileSize(it) }
        return size
    }

    private fun getDownloadUsedBytes(): Long {
        val bilibiliasDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "BILIBILIAS"
        )
        return getFolderSize(bilibiliasDir)
    }

    private fun getCacheTotalBytes(): Long {
        val cacheSize = getFolderSize(koinApplication.cacheDir) +
                (koinApplication.externalCacheDir?.let { getFolderSize(it) } ?: 0L)
        val videoSize = getFolderSize(koinApplication.getExternalFilesDir("video"))
        val audioSize = getFolderSize(koinApplication.getExternalFilesDir("audio"))
        return cacheSize + videoSize + audioSize
    }

    private suspend fun getDownloadFolderSize(): Long {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return getDownloadUsedBytes()
        }
        val uriString = appSettingsRepository.appSettingsFlow.first().downloadUri
        if (uriString.isNotEmpty()) {
            val docFile = DocumentFile.fromTreeUri(koinApplication, uriString.toUri())
            if (docFile != null && docFile.exists() && docFile.isDirectory) {
                return getDocumentFileSize(docFile)
            }
        }
        return getDownloadUsedBytes()
    }

    private fun deleteFolderRecursively(file: File?): Boolean {
        if (file == null || !file.exists()) return true
        if (file.isFile) return file.delete()
        var success = true
        file.listFiles()?.forEach {
            success = success && deleteFolderRecursively(it)
        }
        return file.delete() && success
    }
}
