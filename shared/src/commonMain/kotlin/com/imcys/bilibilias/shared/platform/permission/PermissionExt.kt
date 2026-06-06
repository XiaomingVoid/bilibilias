package com.imcys.bilibilias.shared.platform.permission

import androidx.compose.runtime.Composable

expect fun hasStoragePermission(): Boolean

expect fun shouldRequestStoragePermission(): Boolean


interface LegacyStoragePermissionController {
    val shouldRequest: Boolean
    fun request()
}

interface DownloadDirectoryPermissionController {
    val shouldRequest: Boolean
    fun request()
}

/**
 * 存储权限
 */
@Composable
expect fun rememberLegacyStoragePermissionController(
    onDenied: () -> Unit
): LegacyStoragePermissionController

@Composable
expect fun rememberDownloadDirectoryPermissionController(
    onGranted: (String) -> Unit,
    onDenied: () -> Unit
): DownloadDirectoryPermissionController


interface NotificationPermissionController {
    val hasPermission: Boolean
    val shouldShow: Boolean
    fun request()
}

@Composable
expect fun rememberNotificationPermissionController(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): NotificationPermissionController
