package com.imcys.bilibilias.shared.feature.login

internal expect fun saveQRCodeImageToGalleryByPlatform(qrCodeImageUrl: String?): Boolean

internal expect fun goToScanQRByPlatform(): Boolean