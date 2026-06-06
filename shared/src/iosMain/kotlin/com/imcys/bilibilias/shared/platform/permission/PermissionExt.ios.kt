package com.imcys.bilibilias.shared.platform.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

actual fun hasStoragePermission(): Boolean = true

actual fun shouldRequestStoragePermission(): Boolean = false

@Composable
actual fun rememberDownloadDirectoryPermissionController(
    onGranted: (String) -> Unit,
    onDenied: () -> Unit
): DownloadDirectoryPermissionController {
    return remember {
        object : DownloadDirectoryPermissionController {
            override val shouldRequest: Boolean = false
            override fun request() {
                onDenied()
            }
        }
    }
}

@Composable
actual fun rememberNotificationPermissionController(
    onGranted: () -> Unit,
    onDenied: () -> Unit
): NotificationPermissionController {
    val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
    var hasPermission by remember { mutableStateOf(false) }

    // 检查当前权限状态
    remember {
        notificationCenter.getNotificationSettingsWithCompletionHandler { settings ->
            hasPermission = settings?.authorizationStatus == UNAuthorizationStatusAuthorized
        }
    }

    return remember(notificationCenter) {
        object : NotificationPermissionController {
            override val hasPermission: Boolean
                get() = hasPermission

            override val shouldShow: Boolean = true  // iOS 始终显示

            override fun request() {
                notificationCenter.requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or
                            UNAuthorizationOptionBadge or
                            UNAuthorizationOptionSound
                ) { granted, error ->
                    hasPermission = granted
                    if (granted) onGranted() else onDenied()
                }
            }
        }
    }
}
