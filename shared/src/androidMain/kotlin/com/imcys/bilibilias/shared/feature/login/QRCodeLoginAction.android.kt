package com.imcys.bilibilias.shared.feature.login

import android.app.Application
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import org.koin.mp.KoinPlatform.getKoin
import java.io.File
import java.io.FileOutputStream
import java.net.URL

internal actual fun saveQRCodeImageToGalleryByPlatform(qrCodeImageUrl: String?): Boolean {
    if (qrCodeImageUrl.isNullOrBlank()) return false
    return runCatching {
        val application = getKoin().get<Application>()
        val bitmap = URL(qrCodeImageUrl).openStream().use(BitmapFactory::decodeStream) ?: return false
        saveBitmapToGallery(application, bitmap)
        true
    }.getOrDefault(false)
}

internal actual fun goToScanQRByPlatform(): Boolean {
    return runCatching {
        val application = getKoin().get<Application>()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("bilibili://qrscan")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        application.startActivity(intent)
        true
    }.getOrDefault(false)
}

private fun saveBitmapToGallery(application: Application, bitmap: Bitmap) {
    val contentResolver = application.contentResolver
    val fileName = "QR_${System.currentTimeMillis()}.jpeg"
    val relativePath = "${Environment.DIRECTORY_PICTURES}/BILIBILIAS"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativePath)
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return
        contentResolver.openOutputStream(uri, "w")?.use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }
    } else {
        @Suppress("DEPRECATION")
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val dir = File(picturesDir, "BILIBILIAS")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        }
        val values = ContentValues().apply {
            @Suppress("DEPRECATION")
            put(MediaStore.Images.Media.DATA, file.absolutePath)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}
