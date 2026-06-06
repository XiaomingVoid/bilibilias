package com.imcys.bilibilias.shared.feature.login

import com.imcys.bilibilias.shared.platform.storage.saveImageUrlToPhotos
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

internal actual fun saveQRCodeImageToGalleryByPlatform(qrCodeImageUrl: String?): Boolean {
    return saveImageUrlToPhotos(qrCodeImageUrl)
}

internal actual fun goToScanQRByPlatform(): Boolean {
    return runCatching {
        val url = NSURL.URLWithString("bilibili://qrscan") ?: return false
        return UIApplication.sharedApplication.canOpenURL(url).also { canOpen ->
            if (canOpen) {
                UIApplication.sharedApplication.openURL(url)
            }
        }
    }.getOrDefault(false)
}
