package com.imcys.bilibilias.shared.platform.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.imcys.bilibilias.common.utils.AsRegexUtil
import com.imcys.bilibilias.shared.platform.runtime.koinApplication

actual fun setClipboardText(text: String) {
    val context = koinApplication
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("", text)
    clipboard.setPrimaryClip(clip)
}

actual fun getClipboardText(): String? {
    val context = koinApplication
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
}

actual fun readAndConsumeClipboardText(): String? {
    val context = koinApplication
    val clipboard =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager ?: return null
    val clip = clipboard.primaryClip ?: return null

    val text = clip.getItemAt(0)
        .coerceToText(context)
        ?.toString()
        ?.trim()
        .takeIf { !it.isNullOrEmpty() }
        ?: return null

    if (AsRegexUtil.parse(text) == null) {
        return null
    }

    // 清空，避免重复处理
    clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
    return text
}
