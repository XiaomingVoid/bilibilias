package com.imcys.bilibilias.shared.platform.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberLegacyStoragePermissionController(onDenied: () -> Unit): LegacyStoragePermissionController {
    return remember {
        object : LegacyStoragePermissionController {
            override val shouldRequest: Boolean = false
            override fun request() {
            }
        }
    }
}
