package com.imcys.bilibilias.shared.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.imcys.bilibilias.datastore.*
import com.imcys.bilibilias.shared.platform.clipboard.readAndConsumeClipboardText
import kotlinx.coroutines.delay

private const val CLIPBOARD_READ_DELAY_MS = 180L

/**
 * 处理剪贴板自动识别
 */
@Composable
fun ClipboardAutoHandler(
    appSettings: AppSettings,
    shouldHandleClipboard: () -> Boolean = { true },
    onClipboardText: (String) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val enabledState by rememberUpdatedState(appSettings.enabledClipboardAutoHandling)
    val shouldHandleState by rememberUpdatedState(shouldHandleClipboard)
    val onClipboardTextState by rememberUpdatedState(onClipboardText)

    if (!enabledState || !shouldHandleState()) return

    LaunchedEffect(lifecycleOwner, shouldHandleState) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            delay(CLIPBOARD_READ_DELAY_MS)
            val text = readAndConsumeClipboardText()
            if (!text.isNullOrEmpty()) {
                onClipboardTextState(text)
            }
        }
    }
}
