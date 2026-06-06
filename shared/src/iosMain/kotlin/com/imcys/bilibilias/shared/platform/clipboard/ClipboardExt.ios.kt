package com.imcys.bilibilias.shared.platform.clipboard

import platform.UIKit.UIPasteboard

actual fun setClipboardText(text: String) {
    UIPasteboard.generalPasteboard.setString(text)
}

actual fun getClipboardText(): String? {
    return UIPasteboard.generalPasteboard.string
}

actual fun readAndConsumeClipboardText(): String? {
    return null
}
